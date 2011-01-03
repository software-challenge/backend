class PreliminaryContestant < ActiveRecord::Base

  has_one :contest, :through => :school
  belongs_to :school

  validates_presence_of :school, :name, :location
  validates_uniqueness_of :name, :scope => :school_id 

end
