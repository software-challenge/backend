# Filters added to this controller apply to all controllers in the application.
# Likewise, all the methods added will be available for all controllers.

class ApplicationController < ActionController::Base
  helper :all # include all helpers, all the time
  protect_from_forgery # See ActionController::RequestForgeryProtection for details

  # Scrub sensitive parameters from your log
  filter_parameter_logging :password

  before_filter :fetch_user
  # before_filter :require_current_user

  protected

  attr_accessor :current_user
  helper_method :current_user, :logged_in?

  def require_current_user
    unless logged_in?
      redirect_to login_url
    end
  end

  # get user from DB or fix session
  def fetch_user
    if session[:user_id]
      @current_user = Person.find(session[:user_id])
    end
  rescue ActiveRecord::RecordNotFound
    session[:user_id] = nil
  end

  def logged_in?
    !current_user.nil?
  end
end
