class ScoreDefinition < ActiveRecord::Base
  validates_associated :fragments
  
  has_many :instances, :class_name => "Score", :foreign_key => "definition", :dependent => :destroy
  has_many :fragments, :class_name => "ScoreDefinition::Fragment", :foreign_key => "definition_id", :dependent => :destroy

  def count
    fragments.count
  end

  class Fragment < ActiveRecord::Base
    set_table_name "score_definition_fragments"
    default_scope :order => "position ASC"

    belongs_to :definition, :class_name => "ScoreDefinition"
    has_many :instances, :class_name => "Score::Fragment", :foreign_key => "definition_id", :dependent => :destroy

    validates_uniqueness_of :name, :scope => :definition_id
    validates_presence_of :name

    acts_as_list :scope => :definition_id
  end
end