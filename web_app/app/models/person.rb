require 'digest/sha1'

class Person < ActiveRecord::Base
  include ActionView::Helpers::UrlHelper
  include ActionController::UrlWriter
  RANDOM_HASH_CHARS = ("a".."z").to_a + ("A".."Z").to_a + ("0".."9").to_a
  MINIMUM_PASSWORD_LENGTH = 6

  # acl9
  acts_as_authorization_subject

  # NOTE: use the acl9 methods to find matching roles!
   has_and_belongs_to_many :roles

  has_many :memberships
  has_many :teams, :through => :memberships, :class_name => "Contestant", :source => :contestant
  has_one :email_event, :dependent => :destroy
  
  has_many :schools
  has_many :preliminary_contestants

  # The survey tokens, that are assigned to a single user!
  has_many :survey_tokens

  has_many :login_tokens

  has_one :api_user, :class_name => "Quassum::ApiUser"

  alias :contestants :teams

  validates_presence_of :password_salt
  validates_presence_of :password_hash

  validates_uniqueness_of :email, :message => I18n.t("messages.email_unique")
  validates_presence_of :email
  validates_format_of :email, :with => /^([\w\!\#$\%\&\'\*\+\-\/\=\?\^\`{\|\}\~]+\.)*[\w\!\#$\%\&\'\*\+\-\/\=\?\^\`{\|\}\~]+@((((([a-z0-9]{1}[a-z0-9\-]{0,62}[a-z0-9]{1})|[a-z])\.)+[a-z]{2,6})|(\d{1,3}\.){3}\d{1,3}(\:\d{1,5})?)$/i, :message => "ist keine gültige Adresse"

  validates_presence_of :first_name
  validates_presence_of :last_name

  validates_associated :memberships

  validates_presence_of :password, :on => :create
  validates_length_of :password, :minimum => MINIMUM_PASSWORD_LENGTH, :if => :password

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
  named_scope :helpers,
    :joins => "INNER JOIN people_roles ON people.id = people_roles.person_id INNER JOIN roles ON people_roles.role_id = roles.id", :conditions => "roles.name = 'helper' AND roles.authorizable_id = memberships.contestant_id AND roles.authorizable_type = 'Contestant'"
  named_scope :pupils,
    :joins => "INNER JOIN people_roles ON people.id = people_roles.person_id INNER JOIN roles ON people_roles.role_id = roles.id",
    :conditions => "roles.name = 'pupil' AND roles.authorizable_id = memberships.contestant_id AND roles.authorizable_type = 'Contestant'"
  named_scope :tutors_and_helpers,
    :joins => "INNER JOIN people_roles ON people.id = people_roles.person_id INNER JOIN roles ON people_roles.role_id = roles.id",
    :conditions => "(roles.name = 'tutor' OR roles.name = 'helper') AND roles.authorizable_id = memberships.contestant_id AND roles.authorizable_type = 'Contestant'"

  validates_uniqueness_of :validation_code, :if => :validation_code

  after_save :update_api_user, :if => :api_user
  before_destroy :suspend_api_user, :id => :api_user

  def initialize(*args)
    @save_on_update ||= []
    super
  end

  def generate_login_token
    LoginToken.create(:person => self)
  end

  def after_initialize
    @save_on_update ||= []
    if self.email_event.nil?
      self.email_event = EmailEvent.new
    end
  end

  def update_api_user
    if changes['first_name'] or changes['last_name'] or changes['email']
      api_user.update_attributes({}) 
    end
  end

  def suspend_api_user
    api_user.destroy
  end

  def auth_token!
    chars = ("a".."z").to_a + ("A".."Z").to_a + ("0".."9").to_a
    max_offset = rand(20)
    begin
     code = ""
     1.upto(30+max_offset) { |i| code << chars[rand(chars.size-1)]}
     self.auth_token = code
    end while Person.find_by_auth_token self.auth_token
    self.save!
    auth_token
  end

  def get_or_create_api_user
    if api_user
      api_user
    else
      ap = Quassum::ApiUser.create(:person => self, :api_username => name)
      ap
    end
  end

  def other_schools_for_season(season)
    return [] unless season
    schools = []
    season.schools.collect{|s| s.teams}.flatten.each do |team|
      schools << team.school if self.has_role?(:creator, team)  and not self.has_role_for?(team.school)
    end
    schools.uniq
  end

  def schools_for_season(season)
    self.roles_for(School).collect{|r| School.find(r.authorizable_id)}.reject{|s| s.season != season}
  end

  def name
    if (self.first_name != "" && self.last_name != "")
      ("#{self.first_name} #{self.last_name}")
    else
      self.email
    end
  end

  def blocked
    self.hidden
  end

  def has_memberships_in?(contest)
     not memberships.select{|m| m.contestant and m.person and m.contestant.contests.include? contest}.empty?
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
      teams.reject{|t| not t.contests.include?(contest)}
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

  def memberships_for(contest)
    memberships.find_all{|m| m.contests.include? contest}
  end

  def member_of?(contestant)
    not membership_for(contestant).nil?
  end

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
    self.email = self.email.downcase
    if !@administrator.nil? and current_user and current_user.has_role? :administrator
      if @administrator
        self.has_role! :administrator
      else
        self.has_no_role! :administrator
      end
    end
  end

  def validate_code(code) 
    if self.validation_code and code == self.validation_code 
      self.validation_code = nil
      success = self.save
      self.reload
      return success
    else
      return false
    end
  end

  def tokens_for(survey, only_available = false)
    (only_available ? survey.tokens.available : survey.tokens).select{|t| t.allowed_for?(self)}
  end

  def available_tokens_for(survey)
    tokens_for(survey,true)
  end

  def validated?
    self.validation_code == nil
  end

  def available_survey_tokens
    SurveyToken.all.select{|s| s.allowed_for? self}
  end

  def survey_token_available?
    !available_survey_tokens.empty?
  end

  def has_hidden_friendly_encounters?(context)
    fe = teams.visible.without_testers
    if context.is_a? Contest
      fe = fe.for_contest(context)
    elsif context.is_a? Season
      fe  =fe.for_season(context)
    end
    fe.inject(false) {|val, x| val or x.has_hidden_friendly_encounters?}
  end

  def is_member_of_a_team?(context)
    if context.is_a? Contest
      not teams.visible.without_testers.for_contest(context).empty?
    elsif context.is_a? Season
       not teams.visible.without_testers.for_season(context).empty?    else
       false
    end
  end

  Membership::ROLES.each do |role|
    define_method "is_#{role}_for?" do |c|
      m = membership_for(c)
      unless m.nil?
        m.role.name == role
      else
        false
      end
    end
  end 
  
 protected
    before_destroy do |record|
      self.has_no_roles!
    end
end
