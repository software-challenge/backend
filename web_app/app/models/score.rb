class Score < ActiveRecord::Base

  # validations
  validates_presence_of :definition

  # associations
  belongs_to :definition, :class_name => "ScoreDefinition"
  has_many :fragments, :class_name => "Score::Fragment", :foreign_key => "score_id", :dependent => :destroy

  def set!(values)
    raise "values must be an Array" unless values.is_a? Array
    raise "expected values to have #{definition.fragments.count} values, but was #{values.count}" unless definition.fragments.count == values.count

    Score.transaction do
      fragments.destroy_all
      save! if new_record?

      definition.fragments.each_with_index do |definition_fragment, index|
        self.fragments.create!(:score => self, :definition => definition_fragment, :value => values[index])
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

    validates_presence_of :definition
    validates_presence_of :score
    validates_presence_of :value

    belongs_to :score
    belongs_to :definition, :class_name => "ScoreDefinition::Fragment"

    def value_with_precision
      sprintf("%.#{definition.precision}f", value)
    end
  end
end