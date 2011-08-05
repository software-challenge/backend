class Quassum::ApiUser < ActiveRecord::Base
  POSSIBLE_RIGHTS = ["moderate", "edit_all", "comment_all", "see_all", "none"] # TODO: administrator does not seem to work 
  DEFAULT_ROLE = "comment_all"
  validates_presence_of :person
  belongs_to :person  
  validates_presence_of :api_username
#  attr_protected :api_username, :api_token, :api_password, :api_user_id

  before_validation_on_create :generate_api_user_auth, :unless => :api_user_id
  before_validation :set_api_username, :if => :api_token
  before_create :create_api_user, :if => :api_token
  before_update :update_api_user, :if => :api_token
  #before_destroy :suspend_api_user, :if => :api_token
  after_save :fetch_quassum_users
  after_create :set_user_rights, :if => :api_token
 
  def self.find_by_api_user_id(quassum_user_id)
    self.find(:first,:conditions => ["api_user_id = ?", quassum_user_id]) || quassum_users.find{|u| u["id"] == quassum_user_id} 
  end

  def self.quassum_users
    QUASSUM[:cache].read("users") || fetch_quassum_users
  end

  def self.fetch_quassum_users 
    begin 
      users = JSON.parse(Quassum::Api.get_users)["users"]
      QUASSUM[:cache].write("users", users) 
    rescue Exception => e 
      puts e.message
      puts e.backtrace
    end
    users
  end

  def generate_api_user_auth
    self.api_token = ActiveSupport::SecureRandom.hex(10)
    self.api_password =  ActiveSupport::SecureRandom.hex(10)
  end

  def set_api_username
    self.api_username = person.name
  end

  def memberships
    QUASSUM[:cache].read("user_#{id}_memberships") || fetch_memberships
  end

  def fetch_memberships
    return false unless api_user_id 
    begin 
      data = JSON.parse(Quassum::Api.get_user_memberships(api_user_id))["memberships"]
    rescue Exception => e
      puts e.message
      puts e.backtrace
      return false
    end
    memberships = {}
    data.each{|e| memberships[e["project_shortcut"].to_sym] = e["role"].to_sym}
    QUASSUM[:cache].write("user_#{id}_memberships", memberships) 
    memberships
  end

  def set_user_rights(role = DEFAULT_ROLE)
    return false unless POSSIBLE_RIGHTS.include?(role) and api_user_id
    begin
      Quassum::Api.set_user_rights(api_user_id, QUASSUM[:project_slug], role, QUASSUM[:user][:token], QUASSUM[:user][:password])
    rescue Exception => e
      puts e.message
      puts e.backtrace
      return false
    end
    QUASSUM[:cache].delete("user_#{id}_memberships") 
    true
  end

  def create_api_user
    return true if self.api_user_id
    resp = Quassum::Api.create_user(QUASSUM[:user][:token], QUASSUM[:user][:password], {:password => api_password, :authentication_token => api_token, :name => api_username, :email => person.email})
    self.class.fetch_quassum_users.each do |u|
      self.api_user_id = u["id"] if u["name"] == self.person.name
    end
    #FIXME: this should work when the api is ok:
    #json = JSON.parse(resp)
    #if json
      #api_user_id = json['id'] # TODO: fix this when create works
    #else 
    #  raise "Error creating api user!"
    #end
  end

  def update_api_user
    Quassum::Api.update_user(QUASSUM[:user][:token], QUASSUM[:user][:password], {:password => api_password, :authentication_token => api_token, :name => api_username, :email => person.email})
  end

  def suspend_api_user
    Quassum::Api.update_user(QUASSUM[:user][:token], QUASSUM[:user][:password], {:password => api_password, :authentication_token => api_token, :name => api_username, :email => person.email, :suspended => true})
  end

  def fetch_quassum_users
    self.class.fetch_quassum_users
  end

  def direct_user?
    !api_token
  end

  def self.possible_api_user?(person)
    roles = person.roles.map{|r| r.name.to_sym}.uniq
    return false if [:helper, :administrator, :tutor].any?{|r| roles.include?(r)}
    (roles.include?(:teacher) or roles.include?(:pupil)) and not (person.api_user and person.api_user.direct_user?)
  end
end
