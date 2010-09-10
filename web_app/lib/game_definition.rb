class LeagueConfiguration < Struct.new(:rounds)
  def rounds(r = nil)
    @rounds = r if r
    @rounds
  end
end

class ScoreDefinitionField < Struct.new(:name, :options, :callback, :aggregator)
  def precision
    (options[:precision] || 0).to_i
  end

  def ordering
    options[:ordering]
  end

  def worst
    (options[:worst] || 0).to_i
  end
end

class GameDefinitionBuilder
  def initialize
    @definition = GameDefinition.new
    d.league = LeagueConfiguration.new 1
    d.players = 2
    d.test_rounds = 1
  end
  
  attr_reader :definition
  alias d definition
  
  def league(&block)
    d.league.instance_eval(&block)
  end

  def players(count)
    d.players = count
  end

  def test_rounds(count)
    d.test_rounds = count
  end

  def tester(client)
    d.tester = client    
  end 

  def plugin_guid(guid)
    d.plugin_guid = guid
  end

  def finale(&block)
    o = GameDefinitionBuilder.final_day_collector
    o.instance_eval(&block)
    d.final_days = {}
    o.days.each do |data|
      name, options = *data
      options[:name] = name
      d.final_days[name] = options
    end
  end
  
  def round_score(&block)
    o = GameDefinitionBuilder.field_collector
    o.instance_eval(&block)
    d.round_score = ActiveSupport::OrderedHash.new
    o.fields.each do |data|
      name, options = *data
      field = ScoreDefinitionField.new(*data)
      d.round_score[name] = field
    end
  end
 
  # Does two things: 
  # 1. When called it creates the scores for a match
  #    If a block is given, it is used to calculate the score field value. If i.e. :sum => :carrots is given, the score field "carrots" is used. 
  # 2. It also generates an aggregator that is later used to
  # aggregate multiple match results for the ranking
  # This aggregator is automatically generated depending on whether :sum oder :average had been used
  def match_score(&block)
    o = GameDefinitionBuilder.field_collector
    o.instance_eval(&block)
    d.match_score = ActiveSupport::OrderedHash.new
    o.fields.each do |data|
      name, options, block = *data
      inherit = options[:inherit] || options[:sum] || options[:average]
      # How should be aggregated?
      aggregate = options[:aggregate] || (options[:sum] ? :sum : (options[:average] ? :average : nil ))
      
      # Use an existing score field to sum up, no block needed
      if options[:sum]
        raise "can't provide :average/:sum with a block" if block
        block = lambda do |my_scores, their_scores|
          parts = my_scores.collect { |score| score.send options[:sum] }
          parts.inject(0) { |sum, x| sum + x }
        end
      # Use an existing score field to average, no block needed
      elsif options[:average]
        raise "can't provide :average/:sum with a block" if block
        block = lambda do |my_scores, their_scores|
          parts = my_scores.collect { |score| score.send options[:average] }
          sum = parts.inject(0) { |sum, x| sum + x }
          parts.count.zero? ? 0 : (sum / parts.count.to_f)
        end
      end
     
      # Use options already defined in the given round_score field. Automatically done when no block is given (the options :sum or :average are used). So when round_score field ordering is ASC, match_score ordering will be ASC too by default.
      if inherit
        from_field = d.round_score[inherit]
        raise "field #{inherit} does not exist" unless from_field
        # Adds the options from round_score field to these of match_score field. In the case of duplicate entries the match_score field is used.
        options = (from_field.options || {}).merge(options)
      end

      # Now create the aggregator
      aggregator = nil

      case aggregate
      when :sum
        # Just sum up all values
        aggregator = lambda do |elements|
          parts = elements.collect { |score| score.send(name) }
          parts.inject(0) { |sum, x| sum + x }
        end
      when :average
        # Average all scores
        aggregator = lambda do |elements|
          parts = elements.collect { |score| score.send(name) }
          sum = parts.inject(0) { |sum, x| sum + x }
          parts.count.zero? ? 0 : (sum / parts.count.to_f)
        end
      end

      raise "no callback given for #{name}" unless block
      raise "no aggregator given for #{name}" unless block
      
      field = ScoreDefinitionField.new(name, options, block, aggregator)
      d.match_score[name] = field
    end

    if o.main and d.match_score[o.main].nil?
      raise "Mainfield #{o.main} does not exist. Fields available: #{d.match_score.keys.join(',')}"
    else
      d.match_score_main_field = o.main
    end
  end
  
  protected

  def self.final_day_collector
    o = Object.new

    def o.days; @days; end
      
    def o.day(name, options = {})
      @days ||= []
      @days << [name, options]
    end

    return o 
  end

  def self.field_collector
    o = Object.new

    def o.main(x = nil)
      @main = x if x
      @main
    end

    def o.fields; @fields; end
    
    def o.field(name, options = {}, &block)
      @fields ||= []
      @fields << [name, options, block]
    end

    return o
  end
end

class GameDefinition
  @@definitions = []
  
  def self.create(identifier, &block)
    builder = GameDefinitionBuilder.new
    builder.instance_eval(&block)
    definition = builder.definition
    definition.game_identifier = identifier
    definition.freeze
    @@definitions << definition
    definition
  end
  
  def ==(other)
    return false unless other.is_a? GameDefinition
    self.game_identifier.to_sym == other.game_identifier.to_sym
  end
  
  def self.all
    @@definitions
  end

  def generate_worst_result(slots = [])
    result = []
    slots = slots.empty? ? (1..players) : slots
    slots.each do |slot|
      score = []
      round_score.values.each do |frag|
        score << frag.worst
      end
      result << score
    end
    result
  end

  def day_settings_for(dayname)
    return {} if dayname == :contest
    settings = final_days[dayname]
    raise "No settings for day with name #{dayname}" if settings.nil?
    return settings
  end

  def aggregate_rounds(mine, others)
    extend_by_fields(round_score, mine, *others)
    extend_by_cause(mine, *others)
   
    match_score.collect do |k,v|
      v.callback.call(mine, others)
    end
  end

  def aggregate_matches(elements)
    extend_by_fields(match_score, elements)

    # For every fragment, use its aggregator to
    # aggregate the score elements
    match_score.collect do |k, v|
      v.aggregator.call(elements)
    end
  end

  attr_writer :match_score_main_field

  def match_score_main_field
    @match_score_main_field ||= begin
      if match_score.first
        match_score.first.first
      else
        nil
      end
    end
  end
  
  attr_accessor :game_identifier, :league, :players, :round_score, :match_score, :plugin_guid, :test_rounds, :tester, :final_days

  protected

  # a = [0,1,2,"HI"]
  # extend_by_cause([a])
  # a        # => [0,1,2]
  # a.cause  # => "HI"
  def extend_by_cause(*players)
    players.each do |player|
      player.each do |score|
        cause = score.pop
        # use the eigenklass / metaclass to add method per object
        score.define_singleton_method :cause do
          cause
        end
      end
    end
  end

  # add simple accessors for the score arrays
  # players = array of array of array of Integer
  def extend_by_fields(field_definition, *players)
    # For every score entry, make the fragments accessible by their name
    field_definition.values.each_with_index do |field, i|
      players.each do |scores|
        scores.each do |score|
          # use the eigenklass / metaclass to add method per object
          score.define_singleton_method field.name do
            score[i]
          end
        end
      end
    end
  end
end

Dir[Rails.root.join('config', 'games', '*.{rb,yml}')].each do |file|
  require file
end
