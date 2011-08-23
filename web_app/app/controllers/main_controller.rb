class AccountNotValidated < Exception; end

class MainController < ApplicationController

  skip_before_filter :require_current_user

  access_control do
    actions :debug, :clear_jobs, :administration, :write_email, :send_email, :events do
      allow :administrator
    end

    actions :do_register, :register, :login, :do_login, :new_password do
      allow all, :if => :not_logged_in?
    end

    action :logout do
      allow logged_in
    end

    action :index do
      allow all
    end
  end


  def register
    @person = Person.new
    @email_event = EmailEvent.new
  end

  def do_register
    if params[:context].present?
      context_type, context_id = params[:context].split(":")
      @context = (context_type == "Season" ? Season : Contest).find_by_id(context_id)
      if @context.is_a? Season
        @season = @context
      elsif @context.is_a? Contest
        @contest = @context
      end
    elsif params[:contest_id]
      @context = @contest
    else 
      @context = current_season
      @season = @context
    end

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
          PersonMailer.deliver_signup_notification(@person, @context, params[:person][:person][:password], true)
          format.html { render "main/notification", :locals => {:tab => @context.class.to_s.underscore, :title => "Registrieren", :message => "Ihr Zugang wurde erstellt. An die angebene E-Mail Adresse wurde eine E-Mail mit einem Bestätigungslink gesendet. Um ihren Zugang nutzen zu können, müssen Sie zunächst diese E-Mail abrufen und den Bestätigungslink besuchen.", :links => [["Weiter", url_for(@context)]] }}
        else
          @person.logged_in = true
          @person.last_seen = Time.now
          session[:user_id] = @person.id
          @person.save
          begin
            PersonMailer.deliver_signup_notification(@person, @context, params[:person][:person][:password], false)
          rescue 
            logger.warn "Registration mail could not be send"
          end
          format.html { render "main/notification", :locals => {:tab => @context.class.to_s.underscore, :title => "Registrieren", :message => 'Ihr Zugang wurde erstellt.', :links => [["Weiter", url_for(@context)]] }}
        end
        format.xml { render :xml => @person }
      else
        format.html { render :action => "register" }
        format.xml { render :xml => @person.errors, :status => :unprocessable_entity }
      end
    end
  end

  # root_url
  def index
    redirect_to current_season || seasons_url
  end
   
  def login
    if logged_in?
      flash[:notice] = I18n.t("messages.already_logged_in")
      redirect_to root_url 
    end
  end

  def new_password
    redirect_to @context || root_url if logged_in?
    @email = params[:user].nil? ? nil : params[:user][:email]
    if @email
      person = Person.find(:first, :conditions => {:email => @email})
      if not person
        flash[:error] = t("messages.no_user_with_adress")
        redirect_to login_url
      elsif person.has_role?(:administrator)
        flash[:error] = t("messages.admin_password_cannot_be_changed")
        redirect_to login_url
      else
        password = ActiveSupport::SecureRandom.base64(6)
        person.password = password
        if person.save
          PersonMailer.deliver_password_reset_notification(person, current_season||@current_contest, password)
          flash[:notice] = t("messages.new_password_sent", :email => @email)
        else
          flash[:error] = "Passwort konnte nicht geändert werden" 
        end  
        redirect_to login_url
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
      if params[:remember_me] == "1"
        cookies.permanent[:auth_token] = person.auth_token!
      end
      person.logged_in = true
      person.last_seen = Time.now
      person.save
      if other_people_in_same_team_logged_in? person
        flash[:notice] = I18n.t "messages.other_teammembers_currently_logged_in"
      elsif person.has_role? :administrator and other_administrators_logged_in? person
        flash[:notice] = I18n.t "messages.other_administrators_currently_logged_in"
      end
      if params[:redirect_url]
          redirect_to url_unescape(params[:redirect_url])
      elsif current_season
          redirect_to current_season
      else
        redirect_to seasons_url
      end
    rescue ActiveRecord::RecordNotFound
      @user = { :email => email, :password => "" }.to_obj
      flash[:notice] = I18n.t("messages.login_invalid")
      redirect_to :action => "login", :redirect_url => params[:redirect_url]
    rescue AccountNotValidated
      @user = { :email => email, :password => "" }.to_obj
      flash[:error] = "Der Zugang wurde noch nicht aktiviert. Bitte besuchen Sie den Link in der Bestätigungs E-Mail"
      redirect_to :action => "login"
    end
  end

  def logout
    if @current_user
      @current_user.logged_in = false
      @current_user.auth_token = nil
      @current_user.save
      session[:user_id] = nil
      cookies.delete(:auth_token) if cookies[:auth_token]
      flash[:notice] = I18n.t("messages.logout_successful")
    else
      flash[:error] = I18n.t("messages.not_logged_in")
    end
    if params[:redirect_url] 
      redirect_to url_unescape(params[:redirect_url])
    else
      redirect_to root_url
    end
  end

  def debug
    @jobs = Delayed::Job.all
    @server_log = `tail -n 10 #{Rails.root.join('log', 'game_server.log')}`
    @manager_log = `tail -n 10 #{Rails.root.join('log', 'sc_manager.log')}`
  end

  def administration
    @page_size = 80
    @events = Event.scoped :limit => @page_size, :order => "created_at DESC"
  end

  def write_email

  end

  def events
    @offset = (params[:offset] || 0).to_i
    @page_size = 40;

    if params[:filter]
      f_type, f_id = params[:filter].split(":")
      if ["Contest","Season"].include? f_type
        type = eval(f_type)
      else
        type = nil
      end

      if type 
        @events = type.find(f_id).events.scoped(:limit => @page_size, :order => "created_at DESC", :offset => @offset)
      end
    else
      @events = Event.scoped :offset => @offset, :limit => @page_size, :order => "created_at DESC" 
    end
    respond_to do |format|
      format.html { render :action => :administration }
      format.js
    end
  end

  def send_email
    people = params[:email][:recipients].map{|r| Person.find_by_id(r)}.compact
    if not (params[:email][:title].blank? or params[:email][:text].blank? or people.empty?) and EventMailer.deliver_custom_email(params[:email][:title], params[:email][:text], people)
      flash[:notice] = "Es wurden erfolgreich #{people.count} Mails versendet!"
      redirect_to :action => :administration
    else
      flash[:error] = "Beim Versenden der Mails ist ein Fehler aufgetreten!"
      render :action => :write_email
    end
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
