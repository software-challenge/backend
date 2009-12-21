class LeagueConfiguration < Struct.new(:rounds)
  def rounds(r = nil)
    @rounds = r if r
    @rounds
  end
end

class ScoreDefinitionField < Struct.new(:name, :options, :callback)
  
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
    d.league.instance_eval &block
  end
  
  def players(p)
    d.players = p
  end
  
  def round_score(&block)
    o = GameDefinitionBuilder.field_collector
    o.instance_eval &block
    d.round_score = {}
    o.fields.each do |data|
      name, options = *data
      field = ScoreDefinitionField.new(*data)
      d.round_score[name] = field
    end
  end
  
  def match_score(&block)
    o = GameDefinitionBuilder.field_collector
    o.instance_eval &block
    d.match_score = {}
    o.fields.each do |data|
      name, options, block = *data
      inherit = options[:inherit] || options[:sum] || options[:average]
      
      if options[:sum]
        raise "can't provide :average/:sum with a block" if block
        block = lambda do |me, rounds|
          my_scores = rounds.scores_for(me)
          parts = my_scores.collect &(options[:sum])
          parts.inject(0) { |sum, x| sum + x }
        end
      elsif options[:average]
        raise "can't provide :average/:sum with a block" if block
        block = lambda do |me, rounds|
          my_scores = rounds.scores_for(me)
          parts = my_scores.collect &(options[:aggregate])
          sum = parts.inject(0) { |sum, x| sum + x }
          sum / parts.count.to_f
        end
      end
      
      if inherit
        from_field = d.round_score[inherit]
        raise "field #{inherit} does not exist" unless from_field
        options = (from_field.options || {}).merge(options)
      end
      
      field = ScoreDefinitionField.new(*data)
      d.match_score[name] = field
    end
    
    puts d.match_score.inspect
  end
  
  protected
  
  def self.field_collector
    o = Object.new
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
    builder.instance_eval &block
    definition = builder.definition
    definition.game_identifier = identifier
    definition.freeze
    @@definitions << definition
  end
  
  def self.all
     @@definitions
  end
  
  attr_accessor :game_identifier, :league, :players, :round_score, :match_score
end