require 'digest/sha1'

class Person < ActiveRecord::Base
  RANDOM_HASH_CHARS = ("a".."z").to_a + ("A".."Z").to_a + ("0".."9").to_a
  
  has_many :memberships
  has_many :contestants, :through => :memberships

  validates_presence_of :password_salt
  validates_presence_of :password_hash

  validates_uniqueness_of :email

  def name
    nick_name || ("#{self.first_name} #{self.last_name}")
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

  def tutor?
    Membership.first(:conditions => ["memberships.person_id = ? AND memberships.tutor = ?", self.id, true])
  end

  def pupil?
    Membership.first(:conditions => ["memberships.person_id = ? AND memberships.tutor = ? AND memberships.teacher = ?", self.id, false, false])
  end

  def membership_for(contestant)
    memberships.first(:conditions => ["membership.contestant_id = ?", contestant.id])
  end
end
