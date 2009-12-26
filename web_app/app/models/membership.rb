class Membership < ActiveRecord::Base

  # constants
  ROLES = %w{teacher tutor pupil}

  # associations
  belongs_to :contestant
  belongs_to :person

  # lower level checks
  validates_presence_of :contestant
  validates_presence_of :person
  validates_inclusion_of :role, :in => ROLES

  # higher level checks
  validates_uniqueness_of :contestant_id, :scope => [:person_id]

  # named scopes
  named_scope :as, lambda { |role|
    role = role.to_s
    raise "membership.as(role): role must be one of #{ROLES.join(',')}" unless ROLES.include?(role)
    { :conditions => { :role => role } }
  }

  def initialize(*args)
    super
    self.role ||= "pupil"
  end
end
