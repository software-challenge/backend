# Filters added to this controller apply to all controllers in the application.
# Likewise, all the methods added will be available for all controllers.

require_dependency 'hash_objectify'

class ApplicationController < ActionController::Base
  helper :all # include all helpers, all the time
  protect_from_forgery # See ActionController::RequestForgeryProtection for details

  # Scrub sensitive parameters from your log
  filter_parameter_logging :password

  before_filter :fetch_user
  before_filter :require_current_user
  before_filter :generate_page_title
  before_filter { |c| Authorization.current_user = c.current_user }

  protected

  attr_accessor :current_user
  helper_method :current_user, :logged_in?

  attr_accessor :current_page_title
  helper_method :current_page_title

  def permission_denied
    flash[:error] = "Zugriff nicht gestattet."
    redirect_to root_url
  end

  def require_current_user
    unless logged_in?
      redirect_to login_url
    end
  end

  # get user from DB or fix session
  def fetch_user
    if session[:user_id]
      @current_user = Person.find(session[:user_id])
      ActiveRecord::Base.current_user = @current_user
    end
  rescue ActiveRecord::RecordNotFound
    session[:user_id] = nil
  end

  def logged_in?
    !current_user.nil?
  end

  def guess_page_title
    begin
      clazz = Kernel.const_get controller_name.classify
    rescue
      return  nil
    end
    
    singular = clazz.name
    plural = singular.pluralize
    if clazz.respond_to? :human_name
      singular = clazz.human_name 
      plural = clazz.human_name(:count => 2)
    end

    case action_name
    when "edit", "update"
      I18n.t("titles.generic.edit", :model => singular, :default => nil)
    when "new", "create"
      I18n.t("titles.generic.new", :model => singular, :default => nil)
    when "show"
      I18n.t("titles.generic.show", :model => singular, :default => nil)
    when "index"
      I18n.t("titles.generic.index", :model => plural, :default => nil)
    else
      nil
    end
  end

  def generate_page_title
    default = guess_page_title
    default ||= "#{controller_name} : #{action_name}"
    @current_page_title = I18n.t("titles.#{controller_name}.#{action_name}", :default => default)
  end
end
