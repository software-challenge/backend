class Score < ActiveRecord::Base
  has_many :fragments, :class_name => "Score::Fragment", :foreign_key => "score_id", :dependent => :destroy

  def score_type
    :round_score
  end

  def definition
    GameDefinition.all.first.send(score_type)
  end

  def set!(values)
    raise "values must be an Array" unless values.is_a? Array

    Score.transaction do
      fragments.destroy_all
      save! if new_record?

      values.each do |value|
        self.fragments.create!(:score => self, :value => values)
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

    belongs_to :score

    def value_with_precision
      sprintf("%.#{score.definitiondefinition.precision}f", value)
    end
  end
end