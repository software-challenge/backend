class AccountNotValidated < Exception; end

class MainController < ApplicationController

  skip_before_filter :require_current_user

  access_control :only => [:debug, :clear_jobs, :administration] do
    allow :administrator
  end


  def register
    @person = Person.new
    @email_event = EmailEvent.new
  end

  def do_register
    @person = Person.create(params[:person][:person])
    @email_event = EmailEvent.create(params[:person][:email_event])
    @person.validation_code = @person.random_hash(25) if ENV['EMAIL_VALIDATION'] == "true"
    success = false
    Person.transaction do
      begin
        success = @person.save! && @email_event.save!
      rescue
        success = false
      end
    end
    # TODO: Generate event
    respond_to do |format|
      if success
        if ENV['EMAIL_VALIDATION'] == "true"
          PersonMailer.deliver_signup_notification(@person, @contest, params[:person][:password], true)
          format.html { render "main/notification", :locals => {:tab => :contest, :title => "Registrieren", :message => "Ihr Zugang wurde erstellt. An die angebene E-Mail Adresse wurde eine E-Mail mit einem Bestätigungslink gesendet. Um ihren Zugang nutzen zu können, müssen Sie zunächst diese E-Mail abrufen und den Bestätigungslink besuchen.", :links => [["Weiter", contest_url(@contest)]] }}
        else
          @person.logged_in = true
          @person.last_seen = Time.now
          session[:user_id] = @person.id
          @person.save
          begin
            PersonMailer.deliver_signup_notification(@person, @contest, params[:person][:password], false)
          rescue 
            logger.warn "Registration mail could not be send"
          end
          format.html { render "main/notification", :locals => {:tab => :contest, :title => "Registrieren", :message => 'Ihr Zugang wurde erstellt.', :links => [["Weiter", contest_url(@contest)]] }}
        end
        format.xml { render :xml => @person }
      else
        format.html { render :action => "register" }
        format.xml { render :xml => @person.errors, :status => :unprocessable_entity }
      end
    end
  end

  def index
    redirect_to :controller => :contests, :action => :show
  end

  def login
    if logged_in?
      flash[:notice] = I18n.t("messages.already_logged_in")
      redirect_to contest_url(@contest)
    end
  end

  def new_password
    redirect_to contest_url(@contest) if logged_in?
    @email = params[:user].nil? ? nil : params[:user][:email]
    if @email
      person = Person.find(:first, :conditions => {:email => @email})
      if not person
        flash[:error] = t("messages.no_user_with_adress")
        redirect_to contest_login_url(@contest)
      elsif person.has_role?(:administrator)
        flash[:error] = t("messages.admin_password_cannot_be_changed")
        redirect_to contest_login_url(@contest)
      else
        password = ActiveSupport::SecureRandom.base64(6)
        person.password = password
        if person.save
          PersonMailer.deliver_password_reset_notification(person, @current_contest, password)
          flash[:notice] = t("messages.new_password_sent", :email => @email)
        else
          flash[:error] = "Passwort konnte nicht geändert werden" 
        end
        redirect_to contest_login_url(@contest)
      end
    end
  end

  def do_login
    email = params[:user][:email].downcase
    password = params[:user][:password]
    begin
      person = Person.find_by_email(email)
      raise ActiveRecord::RecordNotFound unless person
      raise ActiveRecord::RecordNotFound unless person.password_match?(password)
      raise ActiveRecord::RecordNotFound if person.hidden?
      raise AccountNotValidated unless person.validated?
      session[:user_id] = person.id
      person.logged_in = true
      person.last_seen = Time.now
      person.save
      if other_people_in_same_team_logged_in? person
        flash[:notice] = I18n.t "messages.other_teammembers_currently_logged_in"
      elsif person.has_role? :administrator and other_administrators_logged_in? person
        flash[:notice] = I18n.t "messages.other_administrators_currently_logged_in"
      end
      redirect_to contest_url(@contest)
    rescue ActiveRecord::RecordNotFound
      @user = { :email => email, :password => "" }.to_obj
      flash[:notice] = I18n.t("messages.login_invalid")
      redirect_to :action => "login"
    rescue AccountNotValidated
      @user = { :email => email, :password => "" }.to_obj
      flash[:error] = "Der Zugang wurde noch nicht aktiviert. Bitte besuchen Sie den Link in der Bestätigungs E-Mail"
      redirect_to :aciton => "login"
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
    redirect_to contest_url(@contest)
  end

  def debug
    @jobs = Delayed::Job.all
    @server_log = `tail -n 10 #{Rails.root.join('log', 'game_server.log')}`
    @manager_log = `tail -n 10 #{Rails.root.join('log', 'sc_manager.log')}`
  end

  def administration
  end

  def clear_jobs
    Delayed::Job.destroy_all
    flash[:notice] = I18n.t("messages.cleared_all_jobs")
    redirect_to contest_debug_url(@contest)
  end

  def other_people_in_same_team_logged_in?(current_user)
    !current_user.teams.collect(&:people).flatten.select{|p| !p.hidden and (p != current_user) and p.currently_logged_in?}.empty?
  end

  def other_administrators_logged_in?(current_user)
    !Person.administrators.visible.select{|p| (p != current_user) and p.currently_logged_in?}.empty?
  end
end
