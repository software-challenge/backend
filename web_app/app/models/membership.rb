class Membership < ActiveRecord::Base

  # associations
  belongs_to :contestant
  belongs_to :person

  # lower level checks
  validates_presence_of :contestant
  validates_presence_of :person

  # higher level checks
  validates_uniqueness_of :contestant_id, :scope => [:person_id]

  def initialize(*args)
    super
  end

  def roles
    person.roles_for(contestant)
  end
end
