require 'digest/sha1'

class Person < ActiveRecord::Base
  RANDOM_HASH_CHARS = ("a".."z").to_a + ("A".."Z").to_a + ("0".."9").to_a
  MINIMUM_PASSWORD_LENGTH = 6

  # acl9
  acts_as_authorization_subject

  has_many :memberships
  has_many :teams, :through => :memberships, :class_name => "Contestant", :source => :contestant

  alias :contestants :teams

  validates_presence_of :password_salt
  validates_presence_of :password_hash

  validates_uniqueness_of :email
  validates_presence_of :email
  validates_format_of :email, :with => /\A([^@\s]+)@((?:[-a-z0-9]+\.)+[a-z]{2,})\Z/i, :message => "ist keine gültige Adresse"

  validates_presence_of :first_name
  validates_presence_of :last_name
  validates_uniqueness_of :nick_name, :allow_blank => true

  validates_associated :memberships

  with_options :with => /\A[\w]*\Z/, :message => "darf keine Sonderzeichen enthalten" do |alnum|
    alnum.validates_format_of :first_name
    alnum.validates_format_of :last_name
  end

  validates_format_of :nick_name, :with => /\A[[:alnum:] ]*\Z/, :message => "darf nur Buchstaben und/oder Zahlen enthalten"

  validates_presence_of :password, :on => :create
  validates_length_of :password, :minimum => MINIMUM_PASSWORD_LENGTH, :on => :create
  
  def name
    if (self.first_name != "" && self.last_name != "")
      ("#{self.first_name} #{self.last_name}")
    else
      self.email
    end
  end

  # compress whitespaces
  def nick_name=(new_nick)
    new_nick = new_nick.strip
    new_nick = new_nick.gsub(/\s+/, ' ')
    self[:nick_name] = new_nick
  end

  # fake accessor for form-builders
  attr_reader :password

  def password=(new_password)
    unless new_password.blank?
      @password = "*" * new_password.length
      self.password_salt = random_hash()
      self.password_hash = Person.encrypt_password(new_password, self.password_salt)
    end
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

  # expects a hash with :id => { :_delete, :role }
  def teams=(teams)
    raise "hash expected" unless teams.is_a? Hash
    return if teams.empty?

    all = {}.tap do |result|
      self.memberships.each do |m|
        result[m.contestant.id] = m
      end
    end

    teams.each do |k,v|
      contestant_id = k.to_i
      role = v["role"]
      delete = v["_delete"].to_i == 1

      if all[contestant_id]
        all[contestant_id].role_name = role
      else
        all[contestant_id] = Membership.new(:contestant => Contestant.find(k), :person => self, :role_name => role)
      end

      all.delete(contestant_id) if delete
    end

    self.memberships = all.values
  end

  def manageable_teams
    if self.has_role? :administrator
      Contestant.without_testers
    else
      teams
    end
  end

  def membership_for(contestant)
    memberships.first :conditions => { :contestant_id => contestant.id }
  end

  alias member_of? membership_for

  def administrator
    @administrator ||= false
    return @administrator
  end

  def administrator=(true_or_false)
    @administrator = true_or_false
  end
end
