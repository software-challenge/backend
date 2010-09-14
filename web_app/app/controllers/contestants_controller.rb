class ContestantsController < ApplicationController

  before_filter :fetch_contestant

  access_control do

    allow :administrator

    action :index, :show do
      allow all
    end

    action :my, :add_person do
      allow :administrator
      allow :tutor, :teacher, :pupil, :of => :contestant
    end

    action :set_and_get_overall_member_count do
      allow :administrator
      allow :tutor, :teacher, :of => :contestant
    end
  end

  access_control :helper => :may_add_teams? do
    allow :administrator
  end

  access_control :helper => :may_see_details? do
    allow anonymous
    allow :administrator
    allow :tutor, :teacher, :pupil, :of => :contestant
  end

  # GET /contestants
  # GET /contestants.xml
  def index
    @contestants = @contest.contestants.visible.all :order => "location, name"

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @contestants }
    end
  end

  def my
    @contestants = current_user.contestants.visible.for_contest(@contest)

    respond_to do |format|
      format.html { render :action => "index" }
      format.xml  { render :xml => @contestants }
    end
  end

  # GET /contestants/1
  # GET /contestants/1.xml
  def show
    if logged_in?
      redirect_to contest_contestant_people_url(@contest, Contestant.find(params[:id]))
    else
      redirect_to :controller => :matches, :action => :index_for_contestant, :contestant_id => params[:id]
    end
  end

  # GET /contestants/new
  # GET /contestants/new.xml
  def new
    @contestant = Contestant.new
    @contestant.contest = @contest

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @contestant }
    end
  end

  # GET /contestants/1/edit
  def edit
    @contestant = @contest.contestants.find(params[:id])
  end

  # POST /contestants
  # POST /contestants.xml
  def create
    @contestant = Contestant.new(params[:contestant])
    @contestant.contest = @contest

    respond_to do |format|
      if @contestant.save
        flash[:notice] = I18n.t("messages.contestant_successfully_created")
        format.html { redirect_to(contest_contestants_url(@contest)) }
        format.xml  { render :xml => @contestant, :status => :created, :location => @contestant }
      else
        format.html { render :action => "new" }
        format.xml  { render :xml => @contestant.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /contestants/1
  # PUT /contestants/1.xml
  def update
    @contestant = Contestant.find(params[:id])

    respond_to do |format|
      if @contestant.update_attributes(params[:contestant])
        flash[:notice] = I18n.t("messages.contestant_successfully_updated") 
        format.html { redirect_to(contest_contestant_url(@contest, @contestant)) }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @contestant.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /contestants/1
  # DELETE /contestants/1.xml
  def destroy
    @contestant = @contest.contestants.find(params[:id])
    @contestant.destroy

    respond_to do |format|
      format.html { redirect_to(contest_contestants_url(@contest)) }
      format.xml  { head :ok }
    end
  end

  def add_person
    email = params[:email]
    name = params[:name]
    unless email.blank?
      @person = Person.visible.find_by_email(email)
    else
      @person = Person.visible.to_ary.find{|p| p.name == name}
    end
    if @person.nil?
      flash[:error] = I18n.t("messages.person_not_found_by_email", :email => params[:email])
      redirect_to :controller => :people, :action => :new, :contestant_id => params[:contestant_id]
    elsif @person.email == ENV['MR_SMITH'] and not administrator?
      flash[:error] = "Mr. Smith will only follow administrators' invitations"
      redirect_to :controller => :people, :action => :new, :contestant_id => params[:contestant_id]
    else
      if @person.memberships.find_by_contestant_id(@contestant.id)
        flash[:error] = I18n.t("messages.person_already_belongs_to_contestant")
      else
        if @person.memberships.create!(:contestant => @contestant, :role_name => params[:role])
          add_event PersonAddedToContestantEvent.create(:person => @person, :contestant => @contestant, :actor => @current_user)
          flash[:notice] = I18n.t("messages.person_added_to_contestant")
        end
      end

      redirect_to contest_contestant_url(@contest, @contestant)
    end
  end

  def unhide
    if @contestant.disqualified?
      # Requalify
      @contestant.requalify
    else
      @contestant.hidden = false
      @contestant.save!
    end
    redirect_to :back
  end

  def hide
    if @contest.ready?
      # Contest already started, instead of hiding, hide the members and remove contestant from matchdays
      @contestant.disqualify 
      redirect_to :back
    else
      generic_hide(@contestant)
    end
  end

  def set_and_get_overall_member_count
    count = params[:count].to_i
    if count < 0
      render :text => t("messages.only_values_above_zero")
      return
    end
    contestant = Contestant.find(params[:id].to_i)
    contestant.overall_member_count = count
    unless contestant.save
      render :text => t("messages.couldnt_save")
      return
    end
    render :text => count.to_s
  end

  protected

  def fetch_contestant
    @contestant = @contest.contestants.find(params[:id]) if params[:id]
  end
end
