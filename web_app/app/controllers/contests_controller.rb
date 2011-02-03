class ContestsController < ApplicationController

  access_control do

    allow :administrator

    actions :show, :standings, :results, :edit_schedule do
      allow all
    end

    actions :trial_contest, :register_for_trial_contest do
      allow all
    end

  end

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
    @matchdays = @contest.matchdays

    respond_to do |format|
      format.html
      format.xml  { render :xml => @contest }
    end
  end

  def standings
    @matchday = @contest.last_played_matchday

    redirect_to @contest unless @matchday
  end

  def results
    @matchday = @contest.last_played_matchday

    redirect_to @contest unless @matchday
  end

  # GET /contests/new
  # GET /contests/new.xml
  def new
    @new_contest = Contest.new

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @contest }
    end
  end

  # GET /contests/1/edit
  def edit
    
  end

  # POST /contests
  # POST /contests.xml
  def create
    @new_contest = Contest.new(params[:new_contest])

    respond_to do |format|
      if @new_contest.save
        flash[:notice] = I18n.t("messages.contest_created_successfully")
        format.html { redirect_to contest_contests_url(@contest) }
        format.xml  { render :xml => @new_contest, :status => :created, :location => contests_url(@contest) }
      else
        format.html { render :action => "new" }
        format.xml  { render :xml => @new_contest.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /contests/1
  # PUT /contests/1.xml
  def update
    respond_to do |format|
      if @contest.update_attributes(params[:contest])
        flash[:notice] = I18n.t("messages.contest.updated_successfully")
        format.html { redirect_to contest_contests_url(@contest) }
        format.xml  { head :ok, :location => contest_contests_url(@contest) }
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
    raise "not supported"
    
    @contest.destroy

    respond_to do |format|
      format.html { redirect_to(contest_contests_url(@contest)) }
      format.xml  { head :ok }
    end
  end

  def edit_schedule
    redirect_to contest_url(@contest) unless @contest.ready? or administrator?
  end

  def update_schedule
    success = @contest.update_attributes(params[:contest])
    if success
      flash[:notice] = t("messages.contest_updated_successfully")
    end
    redirect_to contest_edit_schedule_url(@contest)
  end

  def reset_matchdays
    @contest.matchdays.destroy_all

    redirect_to contest_edit_schedule_url(@contest)
  end

  def refresh_matchdays
    start_at_param = read_multipart_param(params[:schedule], :start_at)
    start_at = Date.new(*start_at_param.collect{ |x| x.to_i })
    weekdays = params[:schedule][:weekdays].collect { |x| x.blank? ? nil : x.to_i }
    weekdays.compact!
    weekdays.uniq!
    trials = params[:schedule][:trial_days].to_i

    if @contest.matchdays.count.zero?
      @contest.refresh_matchdays!(start_at, weekdays, trials)

      if @contest.matchdays.count.zero?
        flash[:error] = I18n.t("messages.not_enough_contestants_for_creating_contest", :contestant_label => Contestant.human_name(:count => 2))
      end
    else
      flash[:error] = I18n.t("messages.there_is_already_a_schedule")
    end

    @contest.play_automatically = params[:schedule][:play_automatically]
    @contest.save!
    redirect_to contest_edit_schedule_url(@contest)
  end


  def reaggregate
    if not @current_contest.reaggregate
      flash[:error] = I18n.t("messages.matchday_playing")
    end
    
    redirect_to contest_standings_url(@contest)
  end

  def create_trial_contest(contestants=nil)
    if contestants.nil?
      contestants = []
      params[:trial_contest][:contestants].keys.each do |c|
        contestants << Contestant.find(c.to_i)
      end
    end
    @contest.create_trial_contest(contestants) 
    redirect_to trial_contest_contest_url(@contest)
  end

  def destroy_trial_contest
    if @contest.is_trial_contest?
      main_contest = @contest.main_contest
      @contest.destroy
      redirect_to trial_contest_contest_url(main_contest)
    end
  end

  def trial_contest
    unless @contest.trial_contest.nil?
      redirect_to contest_url(@contest.trial_contest)
    end
  end

  def register_for_trial_contest
    contestants = params[:contestants];
    contestants.each do |team,i|
      contestant = Contestant.find(team)
      contestant.participate_at_trial_contest = i
      contestant.save!
    end
    redirect_to trial_contest_contest_url(@contest)
  end

  def set_allow_trial_registration
    allow = params[:allow]
    @contest.allow_trial_registration = allow
    @contest.save!
    redirect_to trial_contest_contest_url(@contest)
  end

  def phases
    
  end

  def move_phase
    direction = params[:direction]
    index = Contest::PHASES.find_index @contest.phase
    if direction == "forth"
      if index < Contest::PHASES.count - 1
        index += 1
      else
        flash[:error] = "Keine weitere Phase vorhanden"
      end
    else
      if index > 0
        index -= 1
      else
        flash[:error] = "Bereits in der ersten Phase"
      end
    end
    @contest.phase = Contest::PHASES[index]
    if @contest.respond_to? "phase_moved_#{direction}_to_#{@contest.phase}"
      @contest.send "phase_moved_#{direction}_to_#{@contest.phase}"
    end
    @contest.save!
    redirect_to phases_contest_url(@contest) 
  end

  protected

  def read_multipart_param(data, key, count = 3)
    (1..count).collect do |i|
      data["#{key}(#{i}i)"]
    end
  end
end
