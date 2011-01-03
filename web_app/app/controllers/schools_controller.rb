class SchoolsController < ApplicationController

  before_filter :fetch_school, :only => [:edit, :show, :update]

  access_control do
    default :deny
    allow :administrator
    action :create, :new do
      allow all
    end
    action :index do
      allow logged_in
    end
    action :show do 
      allow logged_in, :if => :admin_for_school
    end
  end

  def admin_for_school(as = nil)
    administrator? or if as.nil?
      current_user.has_role_for? @school
    else
      as.has_role_for? @school 
    end
  end
  helper_method :admin_for_school

  def fetch_school
    @school = School.find(params[:id])
  end

  def index
    if administrator?
      @schools = @contest.schools 
    else
      @schools = @current_user.schools
    end
  end 

  def show
    respond_to do |format|
      format.html
      format.xml { render :xml => @school }
    end
  end

  def new
    unless @contest.allow_school_reg or administrator?
      redirect_to contest_url(@contest)
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
    redirect_to contest_url(@contest) unless @contest.allow_school_reg or administrator?
    @school = School.create(params[:school])
    @school.contest = @contest
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
        format.html { render "main/notification", :locals => {:tab => :contest, :title => "Schule anmelden", :message => "Die Schule \"#{@school.name}\" wurde erfolgreich angemeldet", :links => [["Weiter", contest_url(@contest)]] } }
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
        format.html { redirect_to contest_school_url(@contest, @school) }
        format.xml { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml { render :xml => @school.errors, :status => :unprocessable_entity }
      end
    end 
  end

end
