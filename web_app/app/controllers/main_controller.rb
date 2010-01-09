class MainController < ApplicationController

  skip_before_filter :require_current_user

  access_control :only => [:debug, :clear_jobs, :administration] do
    allow :administrator
  end

  def index

  end

  def login
    if logged_in?
      flash[:notice] = "Du bist bereits eingeloggt."
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
      redirect_to root_url
    rescue ActiveRecord::RecordNotFound
      @user = { :email => email, :password => "" }.to_obj
      flash[:notice] = "Die Zugangsdaten sind nicht gültig."
      render :action => "login"
    end
  end

  def logout
    session[:user_id] = nil
    flash[:notice] = "Du wurdest abgemeldet."
    redirect_to root_url
  end

  def debug
    @jobs = Delayed::Job.all
    @server_log = `tail -n 10 #{Rails.root.join('log', 'game_server.log')}`
    @manager_log = `tail -n 10 #{Rails.root.join('log', 'sc_manager.log')}`
  end

  def clear_jobs
    Delayed::Job.destroy_all
    flash[:notice] = "Alle Aufträge wurden gelöscht."
    redirect_to debug_url
  end
end