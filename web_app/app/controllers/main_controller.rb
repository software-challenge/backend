class MainController < ApplicationController

  skip_before_filter :require_current_user

  access_control :only => [:debug, :clear_jobs, :administration] do
    allow :administrator
  end

  def index
    redirect_to :controller => :contests, :action => :show
  end

  def login
    if logged_in?
      flash[:notice] = I18n.t("messages.already_logged_in")
      redirect_to root_url
    end
  end

  def do_login
    email = params[:user][:email]
    password = params[:user][:password]

    begin
      person = Person.find_by_email(email)
      raise ActiveRecord::RecordNotFound unless person
      raise ActiveRecord::RecordNotFound unless person.password_match?(password)
      raise ActiveRecord::RecordNotFound if person.blocked?
      session[:user_id] = person.id
      person.logged_in = true
      person.last_seen = Time.now
      person.save
      if other_people_in_same_team_logged_in? person
        flash[:notice] = I18n.t "messages.other_teammembers_currently_logged_in"
      elsif person.has_role? :administrator and other_administrators_logged_in? person
        flash[:notice] = I18n.t "messages.other_administrators_currently_logged_in"
      end
      redirect_to root_url
    rescue ActiveRecord::RecordNotFound
      @user = { :email => email, :password => "" }.to_obj
      flash[:notice] = I18n.t("messages.login_invalid")
      redirect_to :action => "login"
    end
  end

  def logout
    if @current_user
      @current_user.logged_in = false
      @current_user.save
      session[:user_id] = nil
      flash[:notice] = I18n.t("messages.logout_successful")
    else
      flash[:error] = I18n.t("messages.not_logged_in")
    end
    redirect_to root_url
  end

  def debug
    @jobs = Delayed::Job.all
    @server_log = `tail -n 10 #{Rails.root.join('log', 'game_server.log')}`
    @manager_log = `tail -n 10 #{Rails.root.join('log', 'sc_manager.log')}`
  end

  def clear_jobs
    Delayed::Job.destroy_all
    flash[:notice] = I18n.t("messages.cleared_all_jobs")
    redirect_to debug_url
  end

  def other_people_in_same_team_logged_in?(current_user)
    !current_user.teams.collect(&:people).flatten.select{|p| !p.hidden and (p != current_user) and p.currently_logged_in?}.empty?
  end

  def other_administrators_logged_in?(current_user)
    !Person.administrators.visible.select{|p| (p != current_user) and p.currently_logged_in?}.empty?
  end
end
