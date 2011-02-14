# Filters added to this controller apply to all controllers in the application.
# Likewise, all the methods added will be available for all controllers.

require_dependency 'hash_objectify'

class ApplicationController < ActionController::Base
  class NotAllowed < StandardError; end

  helper :all # include all helpers, all the time
  protect_from_forgery # See ActionController::RequestForgeryProtection for details

  # Scrub sensitive parameters from your log
  filter_parameter_logging :password

  before_filter :fetch_user, :fetch_contest, :fetch_fake_test_suite
  append_before_filter :require_current_user
  append_before_filter :check_contest_access

  append_before_filter :generate_page_title
  append_before_filter :set_mailer_options

  attr_accessor :current_user
  hide_action :current_user

  attr_accessor :current_contest
  hide_action :current_contest

  protected

  rescue_from NotAllowed, Acl9::AccessDenied do
    if logged_in?
      # user has not enough rights
      render_optional_error_file 403
    else
      # user might be logged out, due to inactivity or trys to access
      # a restricted area without logging in because of bookmark
      flash[:error] = I18n.t "messages.login_first"
      redirect_to contest_login_url(@contest)
    end
  end

  helper_method :current_user, :logged_in?, :not_logged_in?
  helper_method :administrator?
  helper_method :current_contest

  attr_accessor :current_page_title
  helper_method :current_page_title

  def permission_denied
    flash[:error] = I18n.t("messages.access_denied")
    redirect_to contest_url(@contest)
  end

  def check_contest_access
    if not @contest.nil? and @contest.is_trial_contest?
      if logged_in?
        if administrator? or @current_user.is_member_of_a_team?(@contest.main_contest)
          return true
        end
      end
      redirect_to contest_url(@contest.main_contest)
    end
  end

  def require_current_user
    return true
    unless logged_in?
      redirect_to contest_login_url(@contest)
    end
  end

  # get user from DB or fix session
  def fetch_user
    if session[:user_id]
      @current_user = Person.find(session[:user_id])
      @current_user.last_seen = Time.now
      @current_user.save
      ActiveRecord::Base.current_user = @current_user
    end
  rescue ActiveRecord::RecordNotFound
    session[:user_id] = nil
  end

  def fetch_contest
  #  if params[:contest_id]
  #    @contest = @current_contest = Contest.find_by_subdomain(params[:contest_id])
  #  end

    # for testing, use first contest when no contest could be found by subdomain
  #  if @current_contest.nil?
  #    @contest = @current_contest = Contest.first
  #  end
  #  raise ActiveRecord::RecordNotFound unless @current_contest
  #  parts = request.url.split('/')
  #  id = parts[4]
  #  if parts[3] == "wettbewerb" and Contest.all.inject(false) {|i,c| i |= c.subdomain == parts[4]} 
  #    @contest = @current_contest = Contest.find_by_subdomain(parts[4])
  #  else
  #    @contest = @current_contest = Contest.first
  #  end
    @contest = @current_contest = Contest.first
    if not params[:contest_id].nil? 
      c = Contest.all.find{|c| c.subdomain == params[:contest_id]}
      @contest = @current_contest = c unless c.nil?
    elsif not params[:id].nil?
      c = Contest.all.find{|c| c.subdomain == params[:id]}
      @contest = @current_contest = c unless c.nil?
    end
  end
 
  def fetch_fake_test_suite
    @fake_test_suite = FakeTestSuite.find_by_id(params[:fake_test_suite_id])
  end

  def administrator?
    logged_in? and current_user.has_role?(:administrator)
  end

  def logged_in?
    !!current_user
  end

  def not_logged_in?
    !logged_in?
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

  helper_method :host_for_contest, :url_for_contest

  def host_for_contest(contest)
    "#{request.host}:#{request.port}"
  #  all_parts = request.host.split('.')

    # don't remove the first part if there is no subdomain
    # i.e. localhost or software-challenge.de
    # note that this is not always valid ( test.co.uk )
  #  if (all_parts.size > 1 and all_parts.last == "localhost") or all_parts.size > 2
  #    all_parts.shift
  #  end

  #  all_parts.unshift contest.subdomain
  #  port = (request.port || 80).to_i
  #  port = (port == 80 ? "" : ":#{port}")
  #  "#{all_parts.join('.')}#{port}"
  end

  def url_for_contest(contest)
    #"http://#{host_for_contest(contest)}/"
    "http://#{host_for_contest(contest)}/contests/#{contest.id}/"
  end

  def generic_hide(model_object, name = :name)
    model_object.hidden = true
    if model_object.save
      flash[:notice] = I18n.t("messages.hidden_successfully", :name => model_object.send(name.to_s))
    end
    redirect_to :back
  end

  def add_event(event)
    @contest.events << event
    @contest.save!
    @contest.reload
  end

  def add_email_event!(person, event)
    person.build_email_event if person.email_event.nil?
    person.email_event.update_attributes! event => true
    person.reload
  end

  def remove_email_event!(person, event)
    person.build_email_event if person.email_event.nil?
    person.email_event.update_attributes! event => false
    person.reload
  end

  def set_mailer_options
    ActionMailer::Base.default_url_options[:host] = request.host_with_port
  end

end
