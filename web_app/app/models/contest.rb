class Contest < ActiveRecord::Base
  validates_presence_of :name
  validates_associated :round_score_definition
  validates_associated :match_score_definition
  validates_numericality_of :rounds_per_match, :greater_than => 0, :less_than => 100

  has_many :contestants, :dependent => :destroy
  has_many :matchdays, :dependent => :destroy

  def game_definition
    GameDefinition.all.first
  end

  # through associations

  has_many :matches, :through => :matchdays

  def to_param
    "#{id}-#{name.parameterize}"
  end

  def started?
    Round.first(:joins => "INNER JOIN matches ON matches.id = rounds.match_id " +
        "INNER JOIN matchdays ON matchdays.id = matches.set_id",
      :conditions => ["rounds.played_at IS NOT NULL AND matches.set_type = ? AND matchdays.contest_id = ?", "Matchday", id])
  end

  def refresh_matchdays!
    raise "matchdays exist already" unless matchdays.empty?
    
    Contest.transaction do
      next_date = Date.today
      generate_matchdays.each_with_index do |pairs, day|
        matchday = matchdays.create!(:contest => self, :when => next_date)
        contestants.each do |contestant|
          matchday.slots.create!(:contestant => contestant)
        end
        pairs.each do |contestants|
          match = matchday.matches.create!
          contestants.each do |contestant|
            if contestant
              match.slots.create!(:matchday_slot => matchday.slots.first(:conditions => { :contestant_id => contestant.id }))
            else
              match.slots.create!
            end
          end
          round_count = 0
          while round_count < rounds_per_match
            (0...match.slots.count).to_a.permute do |permutation|
              round_count = round_count + 1
              round = match.rounds.create!
              permutation.each do |slot_index|
                round.slots.create!(:match_slot => match.slots[slot_index])
              end
              break if round_count >= rounds_per_match
            end
          end
          (1..rounds_per_match).each do
            
          end
        end
        next_date += 1
      end
    end
  end

  protected

  # generates all matchdays (round-robin tournament)
  # NOTE: (later) how about swiss-system instead of round-robin
  def generate_matchdays
    result = []
    list = contestants.all
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

  def self.active
    Contest.first(:order => "active DESC")
  end
end
