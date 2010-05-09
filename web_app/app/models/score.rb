class Score < ActiveRecord::Base
  
  validates_inclusion_of :game_definition, :in => %w{HaseUndIgel}
  validates_inclusion_of :score_type, :in => %w{round_score match_score}

  has_many :fragments, :class_name => "Score::Fragment", :foreign_key => "score_id", :dependent => :destroy

  validates_presence_of :cause, :if => :round_score?

  def definition
    GameDefinition.all.first.send(score_type)
  end 

  def round_score?
    score_type.to_s == "round_score"
  end

  def round_slot
    if round_score?
      RoundSlot.find(:first, :conditions => {:score_id => self.id})
    else
      nil
    end
  end

  def set!(values, cause = nil, error_msg = "")
    raise "values must be an Array" unless values.is_a? Array
    raise "values length was #{values.size}, expected: #{definition.size}" unless values.size == definition.size

    Score.transaction do
      self.cause = cause
      self.error_message = error_msg
      fragments.destroy_all
      save! if new_record?

      definition.values.each_with_index do |fragment,i|
        self.fragments.create!(:score => self, :fragment => fragment.name.to_s, :value => values[i])
      end

      save!
    end
  end

  def to_a
    fragments.collect do |fragment|
      fragment.value
    end
  end

  def to_a_with_precision
    fragments.collect do |fragment|
      fragment.value_with_precision
    end
  end

  class Fragment < ActiveRecord::Base
    set_table_name "score_fragments"

    validates_presence_of :score
    validates_presence_of :value
    
    validates_presence_of :fragment
    validates_format_of :fragment, :with => /\A[a-z0-9_]*\Z/

    belongs_to :score

    def value_with_precision
      precision = score.definition[fragment.to_sym].precision
      sprintf("%.#{precision}f", value)
    end
  end
end
