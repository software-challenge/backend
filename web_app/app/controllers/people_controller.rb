class PeopleController < ApplicationController

  cache_sweeper :person_sweeper, :only => [:update, :create, :remove, :hide, :unhide]

  protected
  
  before_filter :fetch_context
  before_filter :fetch_contestant, :only => [:people_for_contestant, :remove]
  before_filter :try_fetch_contestant
  before_filter :fetch_person, :only => [:edit, :update, :hide, :unhide, :remove, :ticket_settings, :update_ticket_settings]

  access_control do
    allow :administrator

    action :show do
      allow logged_in
    end

    action :validate_code do
      allow all
    end

    actions :new, :create, :invite do
      allow :administrator, :teacher, :tutor, :helper
    end

    action :people_for_contestant do
      allow :administrator
      allow :tutor, :helper, :teacher, :pupil, :of => :contestant
    end

    actions :edit, :update do
      allow :administrator
      allow logged_in, :if => :same_person
      allow :tutor, :helper, :of => :contestant
    end

    action :remove do
      allow :administrator
      allow :tutor, :helper, :teacher, :of => :contestant
    end

  end

  access_control :helper => :may_edit_person? do
    allow :administrator
    allow logged_in, :if => :same_person
  end

  access_control :helper => :may_add_person? do
    allow :administrator
    allow :tutor, :helper, :teacher, :of => :contestant
  end

  access_control :helper => :may_remove_from_contestant? do
    allow :administrator
    allow :tutor, :helper, :teacher, :of => :contestant
  end

  access_control :may_access_contestant_people_list?, :filter => false do
    allow :administrator
    allow :tutor, :helper, :teacher, :of => :contestant
  end

  def same_person(as = nil)
    if as.nil?
      current_user == @person
    else
      current_user == as
    end
  end
  helper_method :same_person

  access_control :helper => :may_see_person_details? do
    allow :administrator
    allow :pupil, :of => :person
    allow :teacher, :helper, :tutor, :of => :person
  end

  # NOTE: requires a contestant to be given
  def fetch_contestant
    # contestant needs to be fetched before authorization control
    @contestant = Contestant.find(params[:contestant_id])
  end

  # tries to fetch the contestant if available
  def try_fetch_contestant
    if params[:contestant_id]
      @contestant ||= Contestant.find(params[:contestant_id])
    end
  end

  def fetch_person
    # person needs to be fetched before authorization control
    @person = Person.find(params[:id])
  end

  public

  def validate_code
    @person = Person.find(params[:id])
    success = @person.validate_code(params[:code])
    respond_to do |format|
      if success
        @person.logged_in = true
        @person.last_seen = Time.now
        session[:user_id] = @person.id
        @person.save
        format.html do
          links = []
          links << ["Zur Hauptseite", url_for(Season.public.last || Contest.public.last)] if Season.public.last or Contest.public.last
          links << ["Jetzt unverbindlich eine Schule anmelden", new_season_school_url(Season.public.last)] if Season.public.last
          render "main/notification", :locals => {:tab => :contest, :title => "Zugang aktivieren", :message => "Ihr Zugang wurde erfolgreich aktiviert. Sie wurden automatisch eingeloggt und können die Funktionen des Wettkampfsystems nun nutzen.", :links => links }
        end

        format.xml { render :xml => @person }  
      else
        format.html { render "main/notification", :locals => {:tab => :contest, :title => "Zugang aktivieren", :message => "Der Zugangscode ist ungültig. Ihr Zugang konnte daher nicht aktiviert werden.", :links => [["Zur Hauptseite", root_url()]] }}
        format.xml { render :xml => @person }
      end
    end
  end

  # GET /people
  # GET /people.xml
  def index
    @people = Person.visible :order => ["email ASC","lower(last_name) ASC"]
    @hidden_people = Person.hidden :order => ["email ASC","lower(last_name) ASC"]

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @people }
    end
  end

  def people_for_contestant
    @people = @contestant.people.visible.all :order => "last_name ASC"
    @people_by_role = {:teacher => [], :helper => [], :tutor => [], :pupil => []}

    @people.each do |person|
      @people_by_role[person.membership_for(@contestant).role_name.to_sym] << person
    end

    respond_to do |format|
      format.html
      format.xml  { render :xml => @people }
    end
  end

  # GET /people/1
  # GET /people/1.xml
  def show
    @person = Person.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @person }
    end
  end

  # GET /people/new
  # GET /people/new.xml
  def new
    @person = Person.new

    if params[:contestant_id]
      @contestant = Contestant.find params[:contestant_id]
      @person.teams = { params[:contestant_id] => {:_delete => false, :role => :pupil} }
    end

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @person }
    end
  end

  def invite
    self.new
  end

  # GET /people/1/edit
  def edit
    # @person is fetched in before_filter
  end

  # POST /people
  # POST /people.xml
  def create
    # cleanup params
    person_params = params[:person].clone

    @person = Person.new(person_params)
    success = @person.save

    respond_to do |format|
      if success

        if @contest 
          add_event PersonCreatedEvent.create(:person => @person, :creator => @current_user)
        end

        if params[:send_notification] == "1"
          PersonMailer.deliver_signup_notification(@person, @current_contest, person_params[:password],false,url_for(@context))
        end
        flash[:notice] = @person.name + " " + I18n.t("messages.created_successfully")
        format.html do
          if params[:contestant_id] and !@person.teams.visible.empty?
            redirect_to(:action => :people_for_contestant, :contestant_id => @person.teams.visible.first.to_param)
          else
            redirect_to [@context, :people]
          end
        end
        format.xml  { render :xml => @person, :status => :created, :location => @person }
      else
        format.html { render :action => "new" }
        format.xml  { render :xml => @person.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /people/1
  # PUT /people/1.xml
  def update
    # @person is fetched in before_filter
    # cleanup params
    @contest ||= Contest.public.last
    person_params = params[:person].clone
    person_params[:teams].reject! { |k,v| !current_user.manageable_teams.find(k) } if person_params[:teams]
    unless administrator? or current_user == @person
      person_params.reject! {|k,v| k != "teams"}
    end
    unless administrator?
      person_params[:email] = @person.email
       
      # Does the person want to change it's password?
      unless person_params[:password].empty?
        if @person.password_match? params[:current_password] 
          unless person_params[:password] == params[:new_password_repeat] 
            flash[:error] = "Die beiden Eingaben des neuen Passworts stimmen nicht überein!"
            render :action => "edit"
            return
          end
        else
          flash[:error] = "Sie haben ihr aktuelles Passwort falsch eingegeben!"
          render :action => "edit"
          return
        end
      end
    end
   
    flash[:error] = nil
    success = @person.update_attributes(person_params)

    respond_to do |format|
      if success
        if params[:send_notification] == "1"
          @current_contest ||= @contest
          PersonMailer.deliver_password_reset_notification(@person, @current_contest, person_params[:password])
        end
        flash[:notice] = I18n.t("views.profile_of") + " " +  @person.name + " " + I18n.t("messages.updated_successfully")
        format.html do
          if params[:contestant_id] and !@person.teams.for_contest(@contest).visible.empty?
            redirect_to(:action => :people_for_contestant, :contestant_id => @contestant.id)
          elsif current_user.has_role? :administrator
            redirect_to contest_people_url(@contest)
          else
            redirect_to [@contest, @person]
          end
        end
        format.xml  { head :ok }
      else
        format.html { flash[:error] = "Das Speichern der Änderungen schlug fehl!"; render :action => "edit" }
        format.xml  { render :xml => @person.errors, :status => :unprocessable_entity }
      end
    end
  end

  def hide
    # Don't hide myself
    if @person != current_user
      generic_hide(@person)
    else 
      flash[:error] = "Ein Benutzer kann sich nicht selbst löschen"
    end
  end

  def unhide
    @person.hidden = false
    if @person.save
      flash[:notice] = I18n.t "messages.unhidden_successfully", :name => @person.name
    end
    redirect_to :back
  end

  # DELETE /people/1
  # DELETE /people/1.xml
  def destroy
    # We need the models to display client.authors etc, so we can't just "delete" them.
    raise "Deletion is not supported right now."
  end

  def remove
    @person.memberships.find_by_contestant_id(@contestant.id).destroy
    add_event PersonRemovedFromContestantEvent.create(:person => @person, :contestant => @contestant, :actor => @current_user)
    if may_access_contestant_people_list? @contestant
      redirect_to :action => :people_for_contestant, :contestant_id => @contestant.to_param
    else
      redirect_to :action => :index, :controller => :contestants
    end
  end

  def ticket_settings
    u = Quassum::ApiUser.all.map{|a| a.api_user_id} 
    @available_quassum_accounts = Quassum::ApiUser.quassum_users.reject{|a| u.include? a["id"] }.map{|e| [e["name"],e["id"]]}
  end

  def update_ticket_settings
    u = Quassum::ApiUser.all.map{|a| a.api_user_id} 
    @quassum_user = Quassum::ApiUser.quassum_users.reject{|a| u.include? a["id"] }.find{|a| a["id"] == params[:api_user].to_i} 
    unless @person.api_user and @quassum_user["id"].to_i == @person.api_user.api_user_id
      @person.api_user.destroy if @person.api_user
      @api_user = Quassum::ApiUser.create(:person => @person, :api_user_id => @quassum_user["id"], :api_username => @quassum_user["name"])
    else
      @api_user = @person.api_user
    end
    if @api_user.save 
        flash[:notice] = I18n.t("views.profile_of") + " " +  @person.name + " " + I18n.t("messages.updated_successfully")
        redirect_to [:ticket_settings, @context, @person]
    else
        flash[:error] = "Das Speichern der Änderungen schlug fehl!"
        render :action => "ticket_settings"
    end
  end

  def fetch_context
    @context = @contest ? @contest : @season
  end
end
