class Contestant < ActiveRecord::Base
  
  belongs_to :contest

  has_many :clients
  has_many :memberships
  has_many :people, :through => :memberships
  
end
