class Contest < ActiveRecord::Base
  validates_presence_of :name
  validates_presence_of :subdomain
  validates_presence_of :test_contestant
  validates_presence_of :game_definition

  validates_format_of :subdomain, :with => /\A[a-z0-9-]*\Z/
  validates_uniqueness_of :subdomain

  has_many :all_contestants, :class_name => "Contestant", :dependent => :destroy
  has_many :contestants, :conditions => { :tester => false }
  has_one :test_contestant, :class_name => "Contestant", :conditions => { :tester => true }
  has_many :matchdays, :dependent => :destroy, :conditions => { :type => "Matchday" }
  has_many :custom_matches, :class_name => "CustomMatch", :as => :set, :dependent => :destroy
  has_many :friendly_encounters, :dependent => :destroy
  has_many :events, :dependent => :destroy, :order => "created_at DESC"

  has_one :finale

  def prepare_finale
    Contest.transaction do
      self.finale = Finale.new
      save!
      game_definition.final_days.each do |name,options|
        finale.days.create!(:finale => finale, :name => name.to_s)
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
    return (self.name.downcase.include?("test") or matchdays.all.find{|day| day.when > last_day.when}.nil?)
  end

  def game_definition
    gd = GameDefinition.all.find{|gd| gd.game_identifier == attributes["game_definition"].to_sym} unless attributes["game_definition"].nil?
    gd = GameDefinition.all.first if gd.nil?
    gd
  end

  validates_each :game_definition do |model, attr, value|
    model.errors.add(attr, 'ist unbekannt') unless value
  end

  # through associations

  has_many :matches, :through => :matchdays

  def to_param
   #TODO DELETE "#{id}-#{name.parameterize}"
   #"#{id}"
   "#{subdomain}"
  end

  def after_save
    if game_definition_changed?
      file = Rails.root.join('public', 'clients', game_definition.test_client[:file])

      client = test_contestant.current_client
      client ||= test_contestant.build_current_client(:author => current_user, :contestant => test_contestant)
      client.file = File.open(file)
      client.save!
      client.build_index!

      main_file_entry_name = game_definition.test_client[:executable]
      main_file_entry = client.file_entries(:reload).find_by_file_name(main_file_entry_name)
      raise "main_file_entry #{main_file_entry_name} not found" unless main_file_entry
      client.main_file_entry = main_file_entry
      client.save!

      test_contestant.save!
    end
  end

  def before_validation
    build_test_contestant( :name => game_definition.tester[:contestant_name], :tester => true, :location => "Test" ) unless test_contestant
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

  def refresh_matchdays!(start_at = Date.today, weekdays = 0..6)
    weekdays = weekdays.to_a
    range = (0..6)

    raise "contest already has a schedule" if ready?
    raise "weekdays must at least contain one element in range #{range}" if (range.to_a & weekdays).empty?

    next_date = start_at
    Contest.transaction do
      generate_matchdays.each_with_index do |pairs, day|
        matchday = matchdays.create!(:contest => self, :when => next_date)

        contestants.visible.each do |contestant|
          matchday.slots.create!(:contestant => contestant)
        end

        pairs.each do |contestants|
          # only if all slots are set
          if contestants.nitems == contestants.count
            match = matchday.matches.create!
            match.contestants = contestants
          end
        end

        puts "wd: #{weekdays}"
        begin
          puts next_date.wday
          next_date = next_date.advance(:days => 1)
        end until weekdays.include?(next_date.wday)
      end
    end
  end

  def last_played_matchday
    matchdays.played.published.first(:order => "position DESC")
  end

  def ready?
    !matchdays.empty?
  end

  def begun?
    !matchdays.played.empty?
  end

  def estimate_matchday_count
    if contestants.visible.count.odd?
      contestants.visible.count
    else
      contestants.visible.count - 1
    end
  end

  def game_name
    I18n.t("games.#{game_definition.game_identifier.to_s.underscore}.name")
  end

  protected

  # generates all matchdays (round-robin tournament)
  # NOTE: (later) how about swiss-system instead of round-robin
  def generate_matchdays
    result = []
    list = contestants.visible.all
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
