class Person < ActiveRecord::Base
  has_many :memberships
  has_many :contestants, :through => :memberships
end
