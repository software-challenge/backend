class Contestant < ActiveRecord::Base
  
  belongs_to :contest

  has_many :clients

  has_many :memberships
  has_many :people, :through => :memberships

  has_many :slots, :class_name => "MatchSlot"
  has_many :matches, :through => :slots, :source => :match

  attr_readonly :contest
  attr_protected :contest
  
end
