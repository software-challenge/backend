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

  validates_associated :memberships

  validates_presence_of :password, :on => :create
  validates_length_of :password, :minimum => MINIMUM_PASSWORD_LENGTH, :on => :create

  named_scope :visible, :conditions => {:hidden => false}
  named_scope :hidden, :conditions => {:hidden => true}
  named_scope :administrators,
    :joins => "INNER JOIN people_roles ON people.id = people_roles.person_id INNER JOIN roles ON people_roles.role_id = roles.id",
    :conditions => "roles.name = 'administrator'"

  # NOTE: you can only get teachers, tutors, pupils of a contestant. Person.teacher won't work
  named_scope :teachers,
    :joins => "INNER JOIN people_roles ON people.id = people_roles.person_id INNER JOIN roles ON people_roles.role_id = roles.id",
    :conditions => "roles.name = 'teacher' AND roles.authorizable_id = memberships.contestant_id AND roles.authorizable_type = 'Contestant'"
  named_scope :tutors,
    :joins => "INNER JOIN people_roles ON people.id = people_roles.person_id INNER JOIN roles ON people_roles.role_id = roles.id",
    :conditions => "roles.name = 'tutor' AND roles.authorizable_id = memberships.contestant_id AND roles.authorizable_type = 'Contestant'"
  named_scope :pupils,
    :joins => "INNER JOIN people_roles ON people.id = people_roles.person_id INNER JOIN roles ON people_roles.role_id = roles.id",
    :conditions => "roles.name = 'pupil' AND roles.authorizable_id = memberships.contestant_id AND roles.authorizable_type = 'Contestant'"

  def initialize(*args)
    @save_on_update ||= []
    super
  end

  def after_initialize
    @save_on_update ||= []
  end

  def name
    if (self.first_name != "" && self.last_name != "")
      ("#{self.first_name} #{self.last_name}")
    else
      self.email
    end
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
        @save_on_update << all[contestant_id]
        all[contestant_id].role_name = role
      else
        all[contestant_id] = Membership.new(:contestant => Contestant.find(k), :person => self, :role_name => role)
      end

      if delete
        @save_on_update.delete(all[contestant_id])
        all.delete(contestant_id)
      end
    end

    self.memberships = all.values
  end

  def manageable_teams_for(contest)
    if self.has_role? :administrator
      contest.contestants.visible.without_testers
    else
      teams
    end
  end

  def manageable_teams
    if self.has_role? :administrator
      Contestant.visible.without_testers
    else
      teams
    end
  end

  def membership_for(contestant)
    memberships.first :conditions => { :contestant_id => contestant.id }
  end

  alias member_of? membership_for

  def administrator
    @administrator or self.has_role? :administrator
  end

  def before_update
    @save_on_update.each do |item|
      item.save!
    end
  end

  def administrator=(is_admin)
    unless is_admin.kind_of? TrueClass or is_admin.kind_of? FalseClass
      if is_admin.to_s == "1"
        is_admin = true
      else
        is_admin = false
      end
    end
    @administrator = is_admin
  end

  def currently_logged_in?
    self.logged_in and self.last_seen > ActionController::Base.session_options[:expire_after].ago
  end

  def before_save
    logger.debug "saving with admin = #{@should_be_admin.inspect}"
    if !@administrator.nil? and current_user and current_user.has_role? :administrator
      if @administrator
        self.has_role! :administrator
      else
        self.has_no_role! :administrator
      end
    end
    # hidden people should never be able to login
    if self.hidden
      self.blocked = true
    end
  end

  def has_hidden_friendly_encounters?(contest)
    teams.visible.without_testers.for_contest(contest).inject(false) {|val, x| val or x.has_hidden_friendly_encounters?}
  end

  def is_member_of_a_team?(contest)
    not teams.visible.without_testers.for_contest(contest).nil?
  end
end
