class Membership < ActiveRecord::Base

  ROLES = %w{teacher tutor pupil}

  # associations
  belongs_to :contestant
  belongs_to :person

  # lower level checks
  validates_presence_of :contestant
  validates_presence_of :person

  # higher level checks
  validates_uniqueness_of :contestant_id, :scope => [:person_id]

  def roles
    if person
      person.roles_for(contestant).collect(&:name)
    else
      []
    end
  end

  def role
    roles.first
  end

  def role=(role)
    self[:role] = role
  end

  before_save do |record|
    if record[:role]
      record.person.has_no_roles_for! record.contestant
      record.person.has_role! record[:role], record.contestant
    end
  end

  before_destroy do |record|
    record.person.has_no_roles_for! record.contestant
  end
end
