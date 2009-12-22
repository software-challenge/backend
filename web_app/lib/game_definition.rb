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
end

class GameDefinitionBuilder
  def initialize
    @definition = GameDefinition.new
    d.league = LeagueConfiguration.new 1
    d.players = 2
  end
  
  attr_reader :definition
  alias d definition
  
  def league(&block)
    d.league.instance_eval(&block)
  end
  
  def players(p)
    d.players = p
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
  
  def match_score(&block)
    o = GameDefinitionBuilder.field_collector
    o.instance_eval(&block)
    d.match_score = ActiveSupport::OrderedHash.new
    o.fields.each do |data|
      name, options, block = *data
      inherit = options[:inherit] || options[:sum] || options[:average]
      aggregate = options[:aggregate] || (options[:sum] ? :sum : (options[:average] ? :average : nil ))
      
      if options[:sum]
        raise "can't provide :average/:sum with a block" if block
        block = lambda do |my_scores, their_scores|
          parts = my_scores.collect { |score| score.send options[:sum] }
          parts.inject(0) { |sum, x| sum + x }
        end
      elsif options[:average]
        raise "can't provide :average/:sum with a block" if block
        block = lambda do |my_scores, their_scores|
          parts = my_scores.collect { |score| score.send options[:average] }
          sum = parts.inject(0) { |sum, x| sum + x }
          sum / parts.count.to_f
        end
      end
      
      if inherit
        from_field = d.round_score[inherit]
        raise "field #{inherit} does not exist" unless from_field
        options = (from_field.options || {}).merge(options)
      end

      aggregator = nil

      case aggregate
      when :sum
        aggregator = lambda do |elements|
          parts = elements.collect { |score| score.send(name) }
          parts.inject(0) { |sum, x| sum + x }
        end
      when :average
        aggregator = lambda do |elements|
          parts = elements.collect { |score| score.send(name) }
          sum = parts.inject(0) { |sum, x| sum + x }
          sum / parts.count.to_f
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
  end
  
  def self.all
    @@definitions
  end

  def aggregate_rounds(mine, others)
    # add some dynamic methods
    round_score.values.each_with_index do |field, i|
      others.collect do |other|
        other.each do |score|
          score.define_singleton_method field.name do
            score[i]
          end
        end
      end
      
      mine.each do |my|
        my.define_singleton_method field.name do
          my[i]
        end
      end
    end
      
    match_score.collect do |k,v|
      v.callback.call(mine, others)
    end
  end

  def aggregate_matches(elements)
    # add some dynamic methods
    match_score.values.each_with_index do |field, i|
      elements.each do |my|
        my.define_singleton_method field.name do
          my[i]
        end
      end
    end

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
  
  attr_accessor :game_identifier, :league, :players, :round_score, :match_score
end

Dir[Rails.root.join('config', 'games', '*.{rb,yml}')].each do |file|
  require file
end
