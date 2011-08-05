require 'season_definitions/builders'

class SeasonDefinition
  attr_accessor :phases, :identifier, :subdomain
  @@definitions = []

  def self.create(identifier = "", &block)
    builder = SeasonDefinitionBuilder.new(identifier)
    builder.instance_eval &block
    @@definitions << builder.definition
    builder.definition
  end

  def self.all
    @@definitions
  end

  def self.first
     self.all.first
  end

  def self.last
     self.all.last
  end

  def self.human_name(args = {})     
    c = (args[:count] and args[:count] > 1) ? "other" : "one"
    I18n.t "activerecord.models.season_definition.#{c}" 
  end

  def self.find_by_identifier(identifier)
    self.all.find{|d| d.identifier == identifier} 
  end

  def index_of(phase_identifier)
    current_index = @phases.index{|p|  p.identifier == phase_identifier}
    raise "Phase not found" if current_index.nil?
    current_index
  end

  def find_phase(phase_identifier)
    @phases.find{|ph| ph.identifier == phase_identifier}
  end

  def next_phase(phase_identifier)
    return nil if current_index == @phases.length - 1
    @phases[index_of(phase_identifier) + 1]
  end

  def prev_phase(phase) 
    return nil if current_index == 0
    @phases[index_of(phase_identifier) -1]
  end
  
end



class PhaseConfiguration
  attr_accessor :contests, :identifier, :season

  def initialize(season_definition,identifier = "") 
    @identifier = identifier
    @contests = []
    @season = season_definition
  end
end

class ContestConfiguration
  attr_accessor :identifier, :max_contestants, :subdomain, :phase, :contest_contestant_selectors, :contestant_selectors, :contestants, :finale

  def has_finale?
    !!finale
  end

  def initialize(phase_definition, identifier = "")
    @identifier = identifier
    @contestant_selectors = []
    @contest_contestant_selectors = []
    @phase = phase_definition
  end

  def choose_contestants(contestants = [])
    @contestants = contestants # set for use in selectors!
    @contestants += search_contestants # search contestants imported
    selected = []
    @contestant_selectors.each do |s|
      if s.is_a? Array  # If it's an array match all conditions!
        sel = @contestants
        s.each do |s_part|
          sel &= instance_eval(&s_part)
        end
        selected += sel
      else
        selected << instance_eval(&s)
      end
    end
    selected << @contestants if @contestant_selectors.empty?
    selected.flatten!
    selected.uniq!
    if @max_contestants
      selected = selected.first(@max_contestants)
    end
    selected
  end

  def search_contestants
    c = []
    @contest_contestant_selectors.each do |s|
      c += s.select_contestants
    end
    return c
  end

  def full_subdomain
    season_subdomain = phase.season.subdomain || phase.season.identifier.downcase.gsub(" ", "_")
    season_subdomain+"_"+ (self.subdomain || identifier.downcase.gsub(" ", "_"))
  end

  def build_contest(season_phase)
    contest = Contest.new
    contest.subdomain = full_subdomain
    season_phase.season.game_definition
    contest.name = identifier
    contest.save!
    puts "Created Contest #{contest.subdomain}"
    return contest
  end

  private 

   def find_contest(str)
     Contest.find_by_subdomain(str) || Contest.find_by_name(str)
   end
end

class ContestContestantSelector
   attr_accessor :contest_identifier

   def initialize(contest_identifier, &block)
      @contest_identifier = contest_identifier
      @proc = block
   end

   def select_contestants
      @contestants = []
      @excluded = []
      @contest = contest(contest_identifier)
      instance_eval &@proc
     (@contestants - @excluded).uniq  
   end

   def best(c)
     count = c.to_i
     range(0..count-1)
   end

   def worst(n)
     count = c.to_i
     slots_count = ranking.count
     range(slots_count-1 ..  slots_count-(count+1))
   end

   def all
     @contestants += ranking
   end

   def range(r)
     ranks = ranking 
     r.each do |i|
       @contestants << ranks[i] if ranks[i]
     end
   end
   
   def ranking
      return @contest.last_played_matchday.slots.collect{|s| s.contestant}  if @contest.last_played_matchday
      []
   end

   def exclude_by_id(*contestants)
     contestants.each do |c|
       @excluded << Contestant.find_by_id(c)
     end
   end

   def contest(identifier = nil)
     identifier = identifier || @contest_identifier 
     Contest.find_by_subdomain(identifier) || Contest.find_by_name(identifier)
   end

end

Dir[Rails.root.join('config', 'seasons', '*.{rb}')].each do |file|
  require file
end
