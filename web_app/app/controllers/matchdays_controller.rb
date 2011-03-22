class MatchdaysController < ApplicationController

  access_control :only => [:publish, :reset, :disqualifications] do
    allow :administrator
  end

  def index
    @matchdays = @contest.matchdays

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @matchdays }
      format.json {
        if params[:calendar]
          if params[:start] and params[:end]
            cal_start = Time.at(params[:start].to_i)
            cal_end = Time.at(params[:end].to_i)
            @matchdays = @contest.matchdays.all(:conditions => ["(matchdays.when >= ? AND matchdays.when <= ?)", cal_start, cal_end] )
          else
            @matchdays = @contest.matchdays
          end
          data = @matchdays.collect do |day|
            {
              :id => day.id,
              :title => day.trial? ? "#{day.position}. Probespieltag" : "#{day.position - day.contest.matchdays.trials.count}. Spieltag",
              :start => day.when.strftime('%Y-%m-%d'),
              :end => day.when.strftime('%Y-%m-%d'),
              :allDay => true,
              :className => ((day.running? and administrator?) ? "running" : (day.played? ? (day.published? ? "played" : (administrator? ? "unpublished" : "incoming")) : "incoming")),
              :url => contest_matchday_url(@contest, day),
              :editable => day.moveable?
            }
          end
          render :json => data
        else
          render :json => @contest.matchdays
        end
      }
    end
  end

  def show
    @matchday = @contest.matchdays.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @matchday }
      format.pdf  { render :pdf => "Spieltag_"+@matchday.when.strftime("%d_%m_%Y"), :stylesheets => ['jquery-ui-fixes', 'formtastic', 'formtastic_changes', 'application', 'rails', 'contests', 'clients', 'fullcalendar', 'tabnav'] }
    end
  end

  def disqualifications
    @matchday = @contest.matchdays.find(params[:id])
    @disqualifications = @matchday.disqualifications 
  end

  def standings
    @matchday = @contest.matchdays.find(params[:matchday_id])
    respond_to do |format|
      format.html  # standings.html.erb
      format.pdf { render :pdf => "Rangliste_"+@matchday.when.strftime("%d_%m_%Y"), :stylesheets => ['jquery-ui-fixes', 'formtastic', 'formtastic_changes', 'application', 'rails', 'contests', 'clients', 'fullcalendar', 'tabnav'] }
    end
  end

  def get_standings
    @matchday = @contest.matchdays.find(params[:id])
    ranking = params[:ranking]
    ranking ||= "all"
    render :partial => "matchdays/standings", :locals => {:matchday => @matchday, :ranking => ranking}
  end

  def get_progress
    matchday = Matchday.find(params[:id])
    render :partial => "matchday_progress", :locals => {:matchday => matchday}
  end

  def update
    @matchday = Matchday.find(params[:id])

    respond_to do |format|
      if @matchday.update_attributes(params[:matchday])
        flash[:notice] = I18n.t("messages.matchday_successfully_updated")
        format.html { redirect_to(@matchday) }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @matchday.errors, :status => :unprocessable_entity }
      end
    end
  end

  def destroy
    @matchday = @contest.matchdays.find(params[:id])
    @matchday.destroy

    respond_to do |format|
      format.html { redirect_to(contest_matchdays_url(@contest)) }
      format.xml  { head :ok }
    end
  end

  def reaggregate
    @matchday = @contest.matchdays.find(params[:id])

    if @matchday.running?
      flash[:error] = I18n.t("messages.matchday_already_played")
    else
      Matchday.transaction do
        @matchday.reset!
        @matchday.reload

        @matchday.matches.each do |match|
          match.after_round_played(nil)
        end
      end

      flash[:notice] = I18n.t("messages.matchday_recalculated") 
    end

    redirect_to contest_matchday_url(@contest, @matchday)
  end

  def reset
    @matchday = @contest.matchdays.find(params[:id])

    if @matchday.running?
      flash[:error] = I18n.t("messages.matchday_playing_in_progress")
    else
      Matchday.transaction do
        Match.benchmark("resetting matchday", Logger::DEBUG, false) do
          @matchday.reset!(true)
          @matchday.reload
        end
      end

      flash[:notice] = I18n.t("messages.matchday_resetted")
    end

    redirect_to contest_matchday_url(@contest, @matchday)
  end

  def play
    @matchday = @contest.matchdays.find(params[:id])
    
    if @matchday.running?
      flash[:error] = I18n.t("messages.matchday_playing_in_progress")
    elsif @matchday.played?
      flash[:error] = I18n.t("messages.matchday_already_played")
    else
      @matchday.load_active_clients!
      Matchday.transaction do
        if @matchday.perform_delayed!
          flash[:notice] = I18n.t("messages.job_started_successfully")
        else
          flash[:error] = I18n.t("messages.job_starting_failed")
        end
      end
    end

    if @matchday.class == FinaleMatchday
      redirect_to contest_finale_url(@contest)
    else
      redirect_to contest_matchday_url(@contest, @matchday)
    end
  end

  # POST /contest/1/matchdays/move
  def move
    @matchday = @contest.matchdays.find(params[:id])
    delta = params[:day_delta].to_i

    unless delta.zero?
      Matchday.transaction do
        # necessary to resolve UNIQUENESS constraints
        direction = (delta > 0) ? "DESC" : "ASC"
        @contest.matchdays.all(:conditions => ["position >= ?", @matchday.position] , :order => "position #{direction}").each do |md|
          new_date = md.when.advance(:days => delta)
          md.update_attributes!(:when => new_date)
        end
      end
    end

    render :nothing => true
  end

  # POST /contest/1/matchdays/publish
  def publish
    @matchday = @contest.matchdays.find(params[:id])
    @matchday.publish!
    redirect_to contest_matchday_url(@contest, @matchday)
  end

end
