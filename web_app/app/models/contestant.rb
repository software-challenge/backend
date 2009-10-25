class Contestant < ActiveRecord::Base
  
  belongs_to :contest

  has_many :clients

  has_many :memberships
  has_many :people, :through => :memberships

  has_many :slots, :class_name => "MatchdaySlot"

  attr_readonly :contest
  attr_protected :contest

  def matches
    contest.matches.with_contestant(self)
  end

end
