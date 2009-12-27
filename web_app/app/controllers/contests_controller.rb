class ContestsController < ApplicationController
  # GET /contests
  # GET /contests.xml
  def index
    @contests = Contest.all

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @contests }
    end
  end

  # GET /contests/1
  # GET /contests/1.xml
  def show
    @contest = Contest.find(params[:id])
    @matchdays = @contest.matchdays

    respond_to do |format|
      format.html
      format.xml  { render :xml => @contest }
    end
  end

  def rankings
    @contest = Contest.find(params[:contest_id])
    @matchday = @contest.last_played_matchday

    redirect_to @contest unless @matchday
  end

  def results
    @contest = Contest.find(params[:contest_id])
    @matchday = @contest.last_played_matchday

    redirect_to @contest unless @matchday
  end

  # GET /contests/new
  # GET /contests/new.xml
  def new
    @contest = Contest.new

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @contest }
    end
  end

  # GET /contests/1/edit
  def edit
    @contest = Contest.find(params[:id])
  end

  # POST /contests
  # POST /contests.xml
  def create
    @contest = Contest.new(params[:contest])

    respond_to do |format|
      if @contest.save
        flash[:notice] = 'Contest was successfully created.'
        format.html { redirect_to(@contest) }
        format.xml  { render :xml => @contest, :status => :created, :location => @contest }
      else
        format.html { render :action => "new" }
        format.xml  { render :xml => @contest.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /contests/1
  # PUT /contests/1.xml
  def update
    @contest = Contest.find(params[:id])

    respond_to do |format|
      if @contest.update_attributes(params[:contest])
        flash[:notice] = 'Contest was successfully updated.'
        format.html { redirect_to edit_contest_url(@contest) }
        format.xml  { head :ok }
      else
        @test_contestant = @contest.test_contestant
        format.html { render :action => "edit" }
        format.xml  { render :xml => @contest.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /contests/1
  # DELETE /contests/1.xml
  def destroy
    @contest = Contest.find(params[:id])
    @contest.destroy

    respond_to do |format|
      format.html { redirect_to(contests_url) }
      format.xml  { head :ok }
    end
  end

  def edit_schedule
    @contest = Contest.find(params[:contest_id])
  end

  def reset_matchdays
    @contest = Contest.find(params[:id])
    
    @contest.matchdays.destroy_all

    redirect_to contest_edit_schedule_url(@contest)
  end

  def refresh_matchdays
    @contest = Contest.find(params[:id])

    start_at_param = read_multipart_param(params[:schedule], :start_at)
    start_at = Date.new(*start_at_param.collect{ |x| x.to_i })
    weekdays = params[:schedule][:weekdays].collect { |x| x.blank? ? nil : x.to_i }
    weekdays.compact!
    weekdays.uniq!

    if @contest.matchdays.count.zero?
      @contest.refresh_matchdays!(start_at, weekdays)

      if @contest.matchdays.count.zero?
        flash[:error] = "Es sind nicht genug Teilnehmer vorhanden, um einen Spielplan zu erstellen."
      end
    else
      flash[:error] = "Es liegt bereits ein Spielplan vor."
    end

    redirect_to contest_edit_schedule_url(@contest)
  end

  protected

  def read_multipart_param(data, key, count = 3)
    (1..count).collect do |i|
      data["#{key}(#{i}i)"]
    end
  end
end
