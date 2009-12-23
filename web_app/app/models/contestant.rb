# represents a school
class Contestant < ActiveRecord::Base

  belongs_to :contest

  has_many :clients

  has_many :memberships
  has_many :people, :through => :memberships

  has_many :slots, :class_name => "MatchdaySlot"
  has_many :matchdays, :through => :slots

  belongs_to :current_client, :class_name => "Client"

  attr_readonly :contest
  attr_protected :contest

  validates_presence_of :name
  validates_uniqueness_of :name, :scope => :contest_id
  validates_uniqueness_of :tester, :scope => :contest_id, :if => :tester

  def matches
    contest.matches.with_contestant(self)
  end

end
