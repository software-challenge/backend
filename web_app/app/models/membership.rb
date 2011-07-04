class Membership < ActiveRecord::Base

  ROLES = %w{teacher tutor helper pupil}

  # associations
  belongs_to :contestant
  belongs_to :person

  has_one :contest, :through => :contestant

  # lower level checks
  validates_presence_of :contestant
  validates_presence_of :person
  validates_presence_of :role_name
  validates_inclusion_of :role_name, :in => ROLES

  # higher level checks
  validates_uniqueness_of :contestant_id, :scope => :person_id, :unless => :person_new_record?

  def roles
    if person
      person.roles_for(contestant)
    else
      []
    end
  end


  def role
    roles.first
  end

  def role_name
    @role_name || role.try(:name)
  end

  attr_writer :role_name

  before_save do |record|
    if record.role_name and record.role.try(:name) != record.role_name
      record.person.has_no_roles_for! record.contestant
      record.person.has_role! record.role_name, record.contestant
    end
  end

  before_destroy do |record|
    if record.person
      record.person.has_no_roles_for! record.contestant
    end
  end

  protected

  def person_new_record?
    person and person.new_record?
  end
end
