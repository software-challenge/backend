class Membership < ActiveRecord::Base
  ROLES = %w{contributor moderator administrator}

  belongs_to :contestant
  belongs_to :person

  validates_uniqueness_of :contestant_id, :scope => [:person_id]

  validates_inclusion_of :role, :in => ROLES
end
