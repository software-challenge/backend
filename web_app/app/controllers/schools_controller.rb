class SchoolsController < ApplicationController

  before_filter :fetch_school, :only => [:edit, :show, :update, :get_teams, :surveys, :destroy]

  access_control do
    default :deny
    allow :administrator
    action :create, :new, :new_team, :get_teams do
      allow all
    end
    action :index do
      allow logged_in
    end
    action :show do 
      allow logged_in
    end

    action :surveys do
      allow logged_in
    end
  end

  def admin_for_school?(as = nil)
    if administrator? or as.nil?
      current_user.has_role_for? @school
    else
      as.has_role_for? @school 
    end
  end

  helper_method :admin_for_school?

  def fetch_school
    @school = School.find(params[:id])
  end

  def get_teams
    render :partial => "preliminary_contestants/teams"
  end

  def index
    if administrator?
      @schools = @season.schools 
      @other_schools = []
      @schools_in_states = {}; 
      @schools.each{|s| st = s.state.downcase; @schools_in_states[st] = (@schools_in_states[st] || 0) + 1}; 
    else
      @schools = @current_user.schools_for_season(@season)
      @other_schools = @current_user.other_schools_for_season(@season)
    end
 
  end 

  def show
    respond_to do |format|
      format.html
      format.xml { render :xml => @school }
    end
  end

  def surveys
    @tokens = @school.survey_tokens + @school.preliminary_contestants.collect{|p| p.survey_tokens}
    @tokens.flatten! 
    @tokens = @tokens.select{|token| token.currently_valid? and token.allowed_for? @current_user}
    @tokens.sort!{|a,b| a.complete? ? 1 : -1 }
    flash[:notice] = "Bitte füllen Sie alle verfügbaren Umfragen möglichst bald aus. So ermöglichen sie uns eine bessere Planung des Wettbewerbs!" unless flash[:notice] or @tokens.all?{|t| t.complete?}
  end

  def destroy
    if @school.destroy
      flash[:notice] = "Schule wurde erfolgreich entfernt"
      redirect_to contest_schools_url(@contest)
    else
      flash[:error] = "Beim Entfernen der Schule trat ein Fehler auf!"
      render :action => :show
    end
  end

  def new
    unless (@season.school_registration_allowed? or administrator?) and logged_in?
      redirect_to season_url(@season)
      return
    end
    @school = School.new
    @school.contact = @current_user
    respond_to do |format|
      format.html
      format.xml { render :xml => @school }
    end
  end 

  def edit
  end

  def create
    redirect_to season_url(@season) unless @season.school_registration_allowed? or administrator?
    @school = School.create(params[:school])
    @school.season = @season
    @school.contact = @current_user
    if @school.contact_function == "Andere"
      @school.contact_function = params[:contact_function_other]
    end
    if params[:notify_on_contest_progress]
      add_email_event!(@current_user, :rcv_contest_progress_info) 
    end 
    success = @school.save
    if success
      @current_user.has_role! @school.contact_function, @school
      @current_user.save
      add_event NewSchoolEvent.create(:school => @school)
    end
    respond_to do |format|
      if success
        format.html { render "main/notification", :locals => {:tab => :contest, :title => "Schule anmelden", :message => "Die Schule \"#{@school.name}\" wurde erfolgreich angemeldet.<br><b>Bitte melden Sie nun auch die Teams an, die voraussichtlich teilnehmen werden.</b>", :links => [["Jetzt Teams anmelden", new_season_school_preliminary_contestant_url(@season, @school)]] } }
        format.xml { render :xml => @school }
      else
        format.html { render :action => "new" }
        format.xml { render :xml => @school.errors, :status => :unprocessable_entity }
      end 
    end
  end

  def update
    respond_to do |format|
      success = @school.update_attributes(params[:school])
      
      if success
        flash[:notice] = "Die Schule \"#{@school.name}\" wurde aktualisiert."
        format.html { redirect_to season_school_url(@season, @school) }
        format.xml { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml { render :xml => @school.errors, :status => :unprocessable_entity }
      end
    end 
  end

end
