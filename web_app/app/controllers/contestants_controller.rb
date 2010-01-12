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
      redirect_to contest_contestant_people_url(Contestant.find(params[:id]))
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
        flash[:notice] = 'Contestant was successfully created.'
        format.html { redirect_to(contest_contestant_url(@contestant)) }
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
        flash[:notice] = 'Contestant was successfully updated.'
        format.html { redirect_to(contest_contestant_url(@contestant)) }
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
      format.html { redirect_to(contest_contestants_url) }
      format.xml  { head :ok }
    end
  end

  def add_person
    @person = Person.visible.find_by_email(params[:email])
    if @person.nil?
      flash[:error] = I18n.t("messages.person_not_found_by_email", :email => params[:email])
      redirect_to :controller => :people, :action => :new, :contestant_id => params[:contestant_id]
    else
      @contestant = Contestant.find(params[:contestant_id])
      if @person.memberships.find_by_contestant_id(@contestant.id)
        flash[:error] = I18n.t("messages.person_already_belongs_to_contestant")
      else
        if @person.memberships.create!(:contestant => @contestant, :role_name => params[:role])
          flash[:notice] = I18n.t("messages.person_added_to_contestant")
        end
      end
      redirect_to :controller => :people, :action => :people_for_contestant, :contestant_id => params[:contestant_id]
    end
  end

  def hide
    generic_hide(@contestant)
  end

  protected

  def fetch_contestant
    @contestant = @contest.contestants.find(params[:id]) if params[:id]
  end
end
