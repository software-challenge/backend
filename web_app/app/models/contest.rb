class Contest < ActiveRecord::Base

  validates_presence_of :name
  validates_presence_of :subdomain
  validates_presence_of :game_definition

  validates_format_of :subdomain, :with => /\A[a-z0-9_-]*\Z/
  validates_uniqueness_of :subdomain

  has_and_belongs_to_many :all_contestants, :class_name => "Contestant", :before_add => :is_no_duplicate_name!
  has_and_belongs_to_many :contestants, :conditions => { :tester => false }, :before_add => :is_no_duplicate_name!
  has_and_belongs_to_many :test_contestants, :conditions => { :tester => true }, :class_name => "Contestant", :uniq => true
  #has_one :test_contestant, :class_name => "Contestant", :conditions => { :tester => true }, :dependent => :destroy
  has_many :matchdays, :dependent => :destroy, :conditions => { :type => "Matchday" }
  has_many :custom_matches, :class_name => "CustomMatch", :as => :set, :dependent => :destroy
  has_many :friendly_encounters, :dependent => :destroy, :as => "context"
  has_many :events, :dependent => :destroy, :order => "created_at DESC", :as => "context"
  has_one :trial_contest, :class_name => "Contest", :foreign_key => "trial_contest_id", :dependent => :destroy
  has_one :whitelist, :dependent => :destroy
  has_many :fake_test_suites, :dependent => :destroy
  has_one :finale, :dependent => :destroy
  has_many :matches, :through => :matchdays
  has_many :news_posts, :as => :context
  
  delegate :schools, :preliminary_contestants, :to => :season

  belongs_to :season_phase
  has_one :season, :through => :season_phase

  named_scope :public, :conditions => {:public => true}
  named_scope :hidden, :conditions => {:public => false}

  validate do |record|
    if record.contestants.any?{|con| not record.find_contestants_with_same_name(con).empty? }
      record.errors.add :contests, "may not have two Contestant with the same name"
    end
  end

  before_destroy do |record|
    (record.contestants + record.test_contestants).each do |c|
      c.destroy if c.contests.size == 1 and c.season.nil?
    end
  end

  def find_contestants_with_same_name(con)
     contestants.select{|c| c.name == con.name and c.id != con.id}
  end

  def is_no_duplicate_name!(con)
    raise "Name of Contestant is already in use!" if not find_contestants_with_same_name(con).empty?
    true
  end

  def test_contestant
    test_contestants.first
  end

  def all_friendly_encounters
      friendly_encounters
  end

  def has_season?
    !!season_phase
  end

  def public?
    !!self.public
  end

  def hidden?
    !self.public
  end

  def overall_member_count
    contestants.visible.without_testers.ranked.all.sum(&:overall_member_count)  
  end

  def prepare_finale
    Contest.transaction do
      self.finale = Finale.new
      save!
      game_definition.final_days.each do |name,options|
        finale.days.create!(:finale => finale, :name => name.to_s, :contest_id => self.id)
      end
      finale.save!
    end
  end

  def game_definition_identifier
    if game_definition
      game_definition.game_identifier
    else
      nil
    end
  end

  def regular_phase_finished?
    last_day = last_played_matchday
    return false if last_day.nil?
    return matchdays.all.find{|day| day.when > last_day.when}.nil?
  end

  def game_definition
    gd = GameDefinition.all.find{|gd| gd.game_identifier == attributes["game_definition"].to_sym} unless attributes["game_definition"].nil?
    gd = GameDefinition.all.first if gd.nil?
    gd
  end

  validates_each :game_definition do |model, attr, value|
    model.errors.add(attr, 'ist unbekannt') unless value
  end

  def to_param
   #TODO DELETE "#{id}-#{name.parameterize}"
   #"#{id}"
   "#{subdomain}"
  end

  def after_save
    if game_definition_changed? or test_contestant.current_client.nil?
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

  def before_validation
    if test_contestants.empty?
      con = Contestant.create( :name => game_definition.tester[:contestant_name], :tester => true, :location => "Test" ) 
      con.valid? 
      puts con.errors.full_messages
      con.save!
      test_contestants << con
    end
  end

  def started?
    Round.first(:joins => "INNER JOIN matches ON matches.id = rounds.match_id " +
        "INNER JOIN matchdays ON matchdays.id = matches.set_id",
      :conditions => ["rounds.played_at IS NOT NULL AND matches.set_type = ? AND matchdays.contest_id = ?", "Matchday", id])
  end
  
  def reaggregate
    matchdays.each do |matchday|
      matchday.reaggregate
    end
  end

  def create_friendly_encounter(c1, c2)
    encounter = friendly_encounters.create!(:contest => self)
    encounter.slots.create!(:contestant => c1)
    encounter.slots.create!(:contestant => c2)

    encounter.friendly_match = FriendlyMatch.new(:friendly_encounter => encounter)
    encounter.friendly_match.contestants = [c1, c2]
    encounter
  end

  def refresh_matchdays!(start_at = Date.today, weekdays = 0..6, trials = 0)
    weekdays = weekdays.to_a
    range = (0..6)

    raise "contest already has a schedule" if ready?
    raise "weekdays must at least contain one element in range #{range}" if (range.to_a & weekdays).empty?

    next_date = start_at
    until weekdays.include?(next_date.wday) do
      next_date = next_date.advance(:days => 1)
    end
    Contest.transaction do
      generate_matchdays(trials).each_with_index do |pairs, day|
        matchday = matchdays.create!(:contest => self, :when => next_date)
        if day < trials
          matchday.trial = true
          matchday.save!
        end

        contestants.ranked.visible.each do |contestant|
          matchday.slots.create!(:contestant => contestant)
        end

        pairs.each do |contestants|
          # only if all slots are set
          if contestants.nitems == contestants.count
            match = matchday.matches.create!(:context => self)
            match.contestants = contestants
          end
        end

        #puts "wd: #{weekdays}"
        begin
          #puts next_date.wday
          next_date = next_date.advance(:days => 1)
        end until weekdays.include?(next_date.wday)
      end
    end
  end

  def last_played_matchday
    matchdays.without_trials.played.published.first(:order => "position DESC")
  end

  def ready?
    !matchdays.empty?
  end

  def begun?
    !matchdays.played.empty?
  end

  def estimate_matchday_count
    if contestants.visible.ranked.count.odd?
      contestants.visible.ranked.count
    else
      contestants.visible.ranked.count - 1
    end
  end

  def game_name
    I18n.t("games.#{game_definition.game_identifier.to_s.underscore}.name")
  end

  def upcoming_matchday
    matchdays.not_played.first(:order => "position ASC")
  end

  def ret_clone
    self.clone
  end

  def is_trial_contest?
    not main_contest.nil?
  end

  def main_contest
    Contest.all.find{|c| c.trial_contest == self}
  end

  def create_clone(name, subdomain, conts, options = {})
    raise "Contest with subdomain #{subdomain} already exists" unless Contest.find_by_subdomain(subdomain).nil?
    new_contest = self.clone
    new_contest.name = name
    new_contest.subdomain = subdomain
    new_contest.transaction do
      new_contest.save!
      conts.each do |con|
        unless self.all_contestants.include?(con)
          raise "Contestant is not part of the current contest"
        end
        puts "Cloning contestant ##{con.id}"
        conclone = con.clone
        new_contest.all_contestants << conclone
        conclone.save!
        if options[:clone_clients].nil? or options[:clone_clients]
          if options[:only_current_clients]
            if not con.current_client.nil?
              clients = [con.current_client]
            elsif not con.clients.last.nil?
              clients = [con.clients.last]
            else
              clients = []
            end
          else
            clients = con.clients
          end
          clients.each do |cl|
            puts "Cloning client ##{cl.id}"
            clclone = cl.clone
            clclone.author = cl.author
            clclone.save!
            FileUtils.mkpath File.dirname(clclone.file.path)
            FileUtils.copy cl.file.path, clclone.file.path 
            conclone.clients << clclone
            if con.current_client == cl
              conclone.current_client = clclone
            end
            cl.file_entries.each do |fe|
              if clclone.file_entries.all.find{|f| fe.file_name == f.file_name}.nil?
                feclone = fe.clone
                clclone.file_entries << feclone
                feclone.save!
                if fe == cl.main_file_entry
                  clclone.main_file_entry = feclone
                end 
                clclone.save!
              end
            end
            cl.comments.each do |com|
              comclone = com.clone
              clclone.comments << comclone 
              comclone.save!
            end  
            clclone.save!
          end
        end
        if options[:clone_memberships].nil? or options[:clone_memberships]
          con.memberships.each do |ms|
            msclone = ms.clone
            msclone.contestant_id = conclone.id
            msclone.person_id = ms.person.id
            msclone.role_name = ms.role.try(:name)
            msclone.save!
          end 
        end
        conclone.save!
        new_contest.save!
      end
    end
    new_contest
  end

  def create_trial_contest(conts)
    raise "Contest already has a trial contest" unless trial_contest.nil?
    sd = "trial#{self.subdomain}"
    raise "Contest is a trial contest" if is_trial_contest?
    
    new_name = "#{Contest.human_attribute_name "trial_contest"} #{self.name}"
    new_subdomain = "trial#{self.subdomain}"
    new_contest = self.create_clone(new_name, new_subdomain, conts, :only_current_clients => true)
    self.trial_contest = new_contest
    save!
    new_contest
  end

  def has_replay_viewer?
   !!File.exists?(File.join(RAILS_ROOT,"lib","replay_viewers",game_definition.game_identifier.to_s.underscore,"_viewer.erb"))
  end

  def status
    if finale and finale.finished?
      "finale_finished"
    elsif finale
      "finale_created"
    elsif (not matchdays.empty?) and regular_phase_finished?
      "regular_phase_finished"
    elsif matchdays.first and matchdays.first.played?
      "matchdays_played"
    elsif not matchdays.empty?
      "created_schedule"
    elsif not contestants.empty?
      "assigned_contestants"
    else
      "created"
    end
  end

  protected

  def matchdays_contain?(result, c1, c2)
    result.each do |schedule|
      return true if schedule.include?([c1, c2]) or schedule.include?([c2, c1])
    end
    return false
  end

  # generates all matchdays (round-robin tournament)
  # NOTE: (later) how about swiss-system instead of round-robin
  def generate_matchdays(trials = 0)
    result = []
    list = contestants.ranked.visible.all

    lastNils = []
    # Create trial days with random matchup
    # Make sure no team misses more than one day
    trials.times do |trial|
      conts = list.clone
      schedule = []
      if lastNils.size == conts.size
        lastNils = []
      end
      cont1 = nil
      cont2 = nil
      if list.size.odd?
        while cont2.nil? or lastNils.include?(cont2)
          cont2 = conts.reject{|c| lastNils.include?(c)}.rand
        end
        schedule << [nil, conts.delete(cont2)]
        lastNils << cont2
      end
      until conts.empty?
        c1 = conts.rand
        c2 = conts.rand
        if c1 != c2
          if not matchdays_contain?(result, c1, c2)
            schedule << [conts.delete(c1), conts.delete(c2)]
          else
            if conts.count == 2
              s = schedule.rand
              conts << s[0]
              conts << s[1]
              schedule.delete(s)
            end
          end
        end
      end
      result << schedule
    end

    list << nil if list.size.odd?
    rounds = list.size - 1
    half_size = list.size / 2

    relist = list.clone
    relist.delete_at 0

    rounds.times do |round|
      schedule = []
      first = relist[round % relist.size]
      second = list.first

      schedule << [first, second]

      (1...half_size).each do |i|
        first_index = (round + i) % relist.size
        second_index = (round + relist.size - i) % relist.size
        schedule << [relist[first_index], relist[second_index]]
      end

      result << schedule
    end

    result
  end

end
