require 'digest/sha1'

class Person < ActiveRecord::Base
  RANDOM_HASH_CHARS = ("a".."z").to_a + ("A".."Z").to_a + ("0".."9").to_a
  
  has_many :memberships
  has_many :teams, :through => :memberships, :class_name => "Contestant", :source => :contestant

  alias :contestants :teams

  validates_presence_of :password_salt
  validates_presence_of :password_hash

  validates_uniqueness_of :email
  validates_presence_of :email
  validates_format_of :email, :with => /\A([^@\s]+)@((?:[-a-z0-9]+\.)+[a-z]{2,})\Z/i, :message => "ist keine korrekte Adresse!"

  validates_presence_of :first_name
  validates_presence_of :last_name

  validate_on_update :validate_at_least_one_admin

  def name
    if (self.first_name != "" && self.last_name != "")
      ("#{self.first_name} #{self.last_name}")
    else
      self.email
    end
  end

  def password=(new_password)
    self.password_salt = random_hash()
    self.password_hash = Person.encrypt_password(new_password, self.password_salt)
  end

  def password_match?(password)
    encrypted = self.class.encrypt_password(password, password_salt)
    encrypted == password_hash
  end

  def self.encrypt_password(password, salt)
    Digest::SHA1.hexdigest(password + salt)
  end

  def random_hash(length = 10)
    result = ""
    length.times do
      result << RANDOM_HASH_CHARS[rand(RANDOM_HASH_CHARS.size-1)]
    end
    return result
  end

  def teacher?
    Membership.first(:conditions => ["memberships.person_id = ? AND memberships.teacher = ?", self.id, true])
  end

  def getrole
    if teacher?
      return "Lehrer"
    else
      if tutor?
        return "Tutor"
      else
        if pupil?
          return "Schüler"
        end
      end
    end

    return ""
  end

  def gettutors(team)
    contestant = Contestant.first :joins => "INNER JOIN memberships ON memberships.contestant_id = contestants.id", :conditions => ["memberships.person_id = ? AND contestants.name = ?", self.id, team]

    Person.all :joins => "INNER JOIN memberships ON memberships.person_id = people.id INNER JOIN contestants ON contestants.id = memberships.contestant_id", :conditions => ["contestants.id = ? AND memberships.tutor = ?",contestant.id, true], :order => "first_name ASC"
  end

  def getteams
    teams = Contestant.all :joins => "INNER JOIN memberships ON memberships.contestant_id = contestants.id", :conditions => ["memberships.person_id = ?", self.id]

    result = []

    teams.each do |team|
      if (result.length == 0)
        result.push(team.name)
      else
        result.push(team.name)
      end
     
    end

    return result
  end

  def tutor?
    Membership.first(:conditions => ["memberships.person_id = ? AND memberships.tutor = ?", self.id, true])
  end

  def pupil?
    Membership.first(:conditions => ["memberships.person_id = ? AND memberships.tutor = ? AND memberships.teacher = ?", self.id, false, false])
  end

  def membership_for(contestant)
    Membership.first(:conditions => ["memberships.contestant_id = ?", contestant.id])
  end

  def last_admin?
    return false unless administrator?
    return false if Person.count(:conditions => { :administrator => true }) > 1
    true
  end

  protected

  before_destroy :validate_on_destroy

  def validate_at_least_one_admin
    if administrator_changed? and administrator_was and last_admin?
      errors.add_to_base "Es muss mindestens ein Administrator verbleiben."
    end
  end

  def validate_on_destroy
    raise "can't delete last admin" if last_admin?
  end
end
