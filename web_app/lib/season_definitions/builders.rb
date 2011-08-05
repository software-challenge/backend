class SeasonDefinitionBuilder
  attr_reader :definition

  def initialize(identifier)
    @definition = SeasonDefinition.new
    @definition.identifier = identifier
    @definition.phases = []
  end
  
  def phase(identifier = "phase_#{@definition.phases.length}", &block)
    ph = PhaseConfigurationBuilder.new(@definition, identifier)
    ph.instance_eval &block
    @definition.phases << ph.phase
  end

  def subdomain(subdomain)
    @definition.subdomain = subdomain.to_s
  end

end


class PhaseConfigurationBuilder
  attr_accessor :phase

  def initialize(definition,identifier)
     @definition = definition
     @phase = PhaseConfiguration.new(definition,identifier)
  end

  def contest(name = "contest_#{@phase.contests.length+1}", &block)
     c =  ContestConfigurationBuilder.new(@phase,name)
     c.instance_eval &block
     @phase.contests << c.contest
  end
  
end


class ContestConfigurationBuilder
  attr_accessor :contest
  
  def initialize(phase,identifier="")
    @contest = ContestConfiguration.new(phase,identifier)
    @phase = phase
  end

  protected 
  def contestant_limit(count)
    raise "contestants_limit must be an integer!" unless count.is_a? Integer
    @contest.max_contestants = count 
  end

  def select_contestants(&block)
    if @match_all
      @selectors << block
    else
      @contest.contestant_selectors << block
    end
  end

  def has_finale!
    @contest.finale = true
  end

  def match_all(&block)
    @match_all = true
    @selectors = []
    instance_eval &block
    @contest.contestant_selectors << @selectors
    @match_all = false
  end

  def contestants_with(conditions, value = nil)
    select_contestants do
      contestants.find_all{|c| c.send(conditions) == value}
    end
  end

  def import_contestants_from_each_contest(*contest_identifiers,&block) 
    contest_identifiers.each do |c|
        contestants_for_contest(c,&block)
    end
  end

  def import_contestants_from_contest(contest_identifier,&block)
    selector = ContestContestantSelector.new(contest_identifier, &block)
    @contest.contest_contestant_selectors << selector
  end

  def subdomain(subdomain)
    @contest.subdomain = subdomain.to_s
  end
  
end


