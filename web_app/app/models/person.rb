require 'digest/sha1'

class Person < ActiveRecord::Base
  has_many :memberships
  has_many :contestants, :through => :memberships

  validates_presence_of :password_salt
  validates_presence_of :password_hash

  def password_match?(password)
    encrypted = self.class.encrypt_password(password, password_salt)
    encrypted == password_hash
  end

  def self.encrypt_password(password, salt)
    Digest::SHA1.hexdigest(password + salt)
  end
end
