class PreliminaryContestant < ActiveRecord::Base

  PROBS = ["Sicher", "Wahrscheinlich", "Vielleicht", "Eher nicht", "Sicher nicht"]

  has_one :contest, :through => :school
  belongs_to :school

  validates_presence_of :school, :name
  validates_uniqueness_of :name, :scope => :school_id 
  validates_inclusion_of :participation_probability, :in => PROBS

end
