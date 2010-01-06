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

    Membership.transaction do
      unless teams.empty?
        Contestant.find(teams.keys).each do |team|
          membership_data = teams[team.id.to_s]
          if self.new_record?
            memberships << Membership.new(:contestant => team, :person => self)
          elsif membership_data["_delete"].to_i == 1
            membership_for(team).destroy if membership_for(team)
          elsif membership_for(team)
            membership = membership_for(team)
            membership.role = membership_data["role"]
            membership.save!
          else
            memberships << Membership.new(:contestant => team, :person => self)
          end
        end
      end
    end
  end

  def manageable_teams
    if administrator?
      Contestant.without_testers
    else
      teams
    end
  end

  def membership_for(contestant)
    memberships.first :conditions => { :contestant_id => contestant.id }
  end

  alias member_of? membership_for
end
