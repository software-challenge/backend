class ScoreDefinition < ActiveRecord::Base
  validates_associated :fragments
  
  has_many :instances, :class_name => "Score", :foreign_key => "definition", :dependent => :destroy
  has_many :fragments, :class_name => "ScoreDefinition::Fragment", :foreign_key => "definition_id", :dependent => :destroy, :order => "score_definition_fragments.position ASC"

  def count
    fragments.count
  end

  class Fragment < ActiveRecord::Base
    DIRECTIONS = %w{asc desc none}
    
    set_table_name "score_definition_fragments"
    default_scope :order => "position ASC"
    validates_inclusion_of :direction, :in => DIRECTIONS

    belongs_to :definition, :class_name => "ScoreDefinition"
    has_many :instances, :class_name => "Score::Fragment", :foreign_key => "definition_id", :dependent => :destroy

    validates_uniqueness_of :name, :scope => :definition_id
    validates_presence_of :name
    validates_numericality_of :precision, :greater_than_or_equal_to => 0, :less_than => 10

    acts_as_list :scope => :definition_id

    def orders?
      self.direction != "none"
    end
  end
end