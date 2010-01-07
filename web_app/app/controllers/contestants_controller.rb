class ContestantsController < ApplicationController
  before_filter :fetch_contest

  access_control :except => [:show, :index, :my] do
    default :deny
    allow :administrator
  end

  access_control :only => :my do
    default :deny
    allow logged_in
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
    # unify URL
    unless params[:contest_id]
      redirect_to contest_contestant_url(@contest, @contestant)
      return
    end

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
        format.html { redirect_to(contest_contestant_url(@contest, @contestant)) }
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

  protected

  def fetch_contest
    if params[:contest_id]
      @contest = Contest.find(params[:contest_id])
      @contestant = @contest.contestants.find(params[:id]) if params[:id]
    elsif params[:id]
      @contestant = Contestant.find(params[:id])
      @contest = @contestant.contest
    end
  end
end
