class PreliminaryContestant < ActiveRecord::Base

  PROBS = ["Sicher", "Wahrscheinlich", "Vielleicht", "Eher nicht", "Sicher nicht"]

  has_one :season, :through => :school
  belongs_to :person
  belongs_to :school
  belongs_to :contestant # the contestant generated from this one!
  has_many :survey_tokens, :as => :token_owner, :dependent => :destroy 
  validates_presence_of :school, :name
  validates_uniqueness_of :name, :scope => :school_id 
  validates_inclusion_of :participation_probability, :in => PROBS

  named_scope :participation_confirmed, :conditions => {:participation_confirmed => true} 

  def before_destroy
    Role.find(:all, :conditions => {:authorizable_type => "PreliminaryContestant", :authorizable_id => self.id}).each do |role|
      role.destroy
    end
  end

  def person
    return Person.find(person_id) if person_id 
    school.person
  end
end
