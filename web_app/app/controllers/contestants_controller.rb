class ContestantsController < ApplicationController

  before_filter :fetch_contestant

  access_control do

    allow :administrator

    action :index, :show do
      allow all
    end

    action :my do
      allow :administrator
      allow :tutor, :teacher, :pupil, :of => :contestant
    end

  end

  access_control :helper => :may_add_teams? do
    allow :administrator
  end

  # GET /contestants
  # GET /contestants.xml
  def index
    @contestants = @contest.contestants

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @contestants }
    end
  end

  def my
    @contestants = current_user.contestants.for_contest(@contest)

    respond_to do |format|
      format.html { render :action => "index" }
      format.xml  { render :xml => @contestants }
    end
  end

  # GET /contestants/1
  # GET /contestants/1.xml
  def show
    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @contestant }
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

  protected

  def fetch_contestant
    @contestant = @contest.contestants.find(params[:id]) if params[:id]
  end
end
