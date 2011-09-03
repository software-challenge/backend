class Season < ActiveRecord::Base
  has_many :phases, :class_name => "SeasonPhase", :order => :position, :dependent => :destroy
  has_many :contests, :through => :phases
  has_many :contestants, :dependent => :destroy, :conditions => {:tester => false} # All contestants that are validated
  has_many :test_contestants, :dependent => :destroy, :conditions => {:tester => true}, :class_name => "Contestant"
  has_many :preliminary_contestants, :through => :schools
  has_many :schools, :dependent => :destroy
  has_many :friendly_encounters, :as => :context
  has_many :news_posts, :as => :context
  has_many :events, :as => :context
  belongs_to :current_phase, :class_name => "SeasonPhase"

  belongs_to :recall_survey_template, :class_name => "SurveyNotificationTemplate"
  belongs_to :validation_survey_template, :class_name => "SurveyNotificationTemplate"

  validates_inclusion_of :game_identifier, :in => GameDefinition.all.map{|d| d.game_identifier.to_s}
  validates_inclusion_of :season_definition, :in => SeasonDefinition.all.map{|d| d.identifier}
  validates_uniqueness_of :subdomain
  validates_inclusion_of :recall_survey_code, :in => Survey.all.map{|s| s.access_code}
  validates_inclusion_of :validation_survey_code, :in => Survey.all.map{|s| s.access_code}

  named_scope :public, :conditions => {:public => true}

  liquid_methods :name, :subdomain, :public
  
  def initialize_definition
    self.name = definition.identifier
    self.subdomain = definition.subdomain
    definition.phases.each do |phase_configuration|
      a = SeasonPhase.create(:identifier => phase_configuration.identifier, :season => self)
      a.build_contests
    end
    self.save!
  end

  state_machine :initial => :initialization do

   # When step is not :contest, there is no current_phase
   before_transition :contest => any - :contest do |season|
     season.current_phase = nil
     season.save!
   end

   after_transition :preparation => :contest do |season|
     season.current_phase = season.phases.first
     season.save! if season.current_phase
     season.current_phase.load_contestants
   end

   after_transition :finished => :contest do |season|
     season.current_phase = season.phases.last
     season.save!
   end

   after_transition :contest => :contest do |season,transition|
     if transition.event == :next_step
       season.current_phase = season.current_phase.lower_item
       season.current_phase.load_contestants
     elsif transition.event == :prev_step
       season.current_phase = season.current_phase.higher_item
     end
     season.save!
   end

   state :initialization do
     # The season was created by the admin and is now set up
   end

   state :registration do
    # State the teams should be able to register in the system
   end
  
   state :recall do
    # Recall the teams with the initial survey
   end

   state :validation do
    # Validate the registered teams for the contests and create them as contestants
   end

   state :preparation do
    # The first teams are created and begin to develop their clients, registering new teams is still possible
   end

   state :contest do
    # Contest, where all the phases are played!
   end

   state :finished do
     # Contest is finished nothing to do from now?!
   end

   event :next_step do
    transition :initialization => :registration, :registration => :recall, :recall => :validation, :validation => :preparation, :preparation => :contest
    transition :contest => :finished, :if => :last_phase?
    transition :contest => :contest 
   end

   event :prev_step do
     transition :contest => :contest, :unless => :first_phase?
     transition :contest => :preparation, :preparation => :validation, :validation => :recall, :recall => :registration, :registration => :initialization, :finished => :contest
   end

  end
  
  # Methods that are always accessable
  def school_registration_allowed?
    registration? or recall? or validation? or preparation?
  end

  def team_registration_allowed?
    registration? or recall? or validation? or preparation?
  end

  def participation_confirmation_allowed?
    validation? or preparation?
  end
  
  def surveys_visible?
    recall? or validation? or preparation? or contest? or finished? 
  end

  def schools_visible?
    registration? or recall? or validation? or preparation?
  end

  def preliminary_contestants_visible?
    registration? or recall? or validation? or preparation?
  end

  def contests_visible?
    contest? or finished?
  end

  def published? 
    self.public
  end

  def publish!
    self.public = true
    self.save!
  end
  
  def last_phase_finished?
    false
  end

  def phase_finished?
    false
  end

  def first_phase?
    false
  end

  def unpublish!
    self.public = false
    self.save!
  end

  def recall_survey
    Survey.find_by_access_code(recall_survey_code)
  end

  def validation_survey
    Survey.find_by_access_code(validation_survey_code)
  end

  # Contest methods
  def first_phase?
    contest? and !current_phase.higher_item 
  end

  def last_phase?
    contest? and !current_phase.lower_item
  end

  def phase_finished?
    contest? and current_phase.finished?
  end

  def last_phase_finished?
    contest? and last_phase? and phase_finished?
  end
 
  # Creating a nice Frontend for the GameDefinition and SeasonDefinition
  def definition
    SeasonDefinition.find_by_identifier(season_definition)
  end

  def game_definition
    return nil if not game_identifier
    gd = GameDefinition.all.find{|gd| gd.game_identifier == game_identifier.to_sym} 
    gd
  end

  def game_definition=(gd)
    self.game_identifier = gd.is_a?(GameDefinition) ? gd.game_identifier.to_s : gd.to_s
    self.save! unless new_record?
  end

  def definition=(definition)
    identifier = definition.is_a?(SeasonDefinition) ? definition.identifier : definition
    self.season_definition = identifier.to_s
    self.save! unless new_record?
  end

  def test_contestant
    test_contestants.first
  end

  def after_save
    if test_contestants.empty?
      con = Contestant.create( :name => game_definition.tester[:contestant_name], :tester => true, :location => "Test" ) 
      con.valid? 
      puts con.errors.full_messages
      con.save!
      test_contestants << con
    end
    if game_identifier_changed? or test_contestant.current_client.nil?
      puts "Creating test client"
      file = Rails.root.join('public', 'clients', game_definition.tester[:file])
      puts "Client file: #{file}"

      author = current_user
      author ||= Person.find(1)
      client = test_contestant.current_client
      client ||= test_contestant.build_current_client(:author => author, :contestant => test_contestant)
      puts "Author: #{author}, Contestant: #{test_contestant}"
      client.file = File.open(file)
      client.save!
      client.build_index!

      main_file_entry_name = game_definition.tester[:executable]
      main_file_entry = client.file_entries(:reload).find_by_file_name(main_file_entry_name)
      raise "main_file_entry #{main_file_entry_name} not found" unless main_file_entry
      client.main_file_entry = main_file_entry
      client.save!

      test_contestant.save!
    end
  end

  def states
    [:initialization, :registration, :recall, :validation, :preparation, :contest, :finished]
  end

  def past_state?(state)
    states.index(state) < states.index(self.state.to_sym)
  end

  def overall_member_count
    contestants.visible.without_testers.ranked.all.sum(&:overall_member_count)  
  end

  def has_replay_viewer?
   !!File.exists?(File.join(RAILS_ROOT,"lib","replay_viewers",game_definition.game_identifier.to_s.underscore,"_viewer.erb"))
  end

end
