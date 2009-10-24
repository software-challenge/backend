class MatchdaysController < ApplicationController
  before_filter :fetch_contest

  def index
    @matchdays = @contest.matchdays

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @matchdays }
      format.json {
        if params[:calendar]
          data = @matchdays.collect do |day|
            {
              :id => day.id,
              :title => "#{day.position}. Spieltag",
              :start => day.when.strftime('%Y-%m-%d'),
              :end => day.when.strftime('%Y-%m-%d'),
              :allDay => true,
              :className => (day.played? ? "played" : "incoming"),
              :url => contest_matchday_url(@contest, day)
            }
          end
          render :json => data
        else
          render :json => @matchdays
        end
      }
    end
  end

  def show
    @matchday = @contest.matchdays.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @matchday }
    end
  end

  def update
    @matchday = Matchday.find(params[:id])

    respond_to do |format|
      if @matchday.update_attributes(params[:matchday])
        flash[:notice] = 'Matchday was successfully updated.'
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
      format.html { redirect_to(matchdays_url) }
      format.xml  { head :ok }
    end
  end

  def reaggregate
    @matchday = @contest.matchdays.find(params[:id])

    if @matchday.running?
      flash[:error] = "Der Spieltag wird bereits gespielt."
    else
      Matchday.transaction do
        @matchday.reset!
        @matchday.reload

        @matchday.matches.each do |match|
          match.after_round_played(nil)
        end
      end
    end

    redirect_to contest_matchday_url(@contest, @matchday)
  end

  def play
    @matchday = @contest.matchdays.find(params[:id])

    if @matchday.running?
      flash[:error] = "Der Spieltag wird bereits gespielt."
    else
      Matchday.transaction do
        Match.benchmark("resetting matchday", Logger::DEBUG, false) do
          @matchday.reset!(true)
          @matchday.reload
        end
        
        if @matchday.perform # _delayed!
          flash[:notice] = "Der Auftrag wurde erfolgreich gestartet."
        else
          flash[:error] = "Konnte den Auftrag nicht starten."
        end
      end
    end
    
    redirect_to contest_matchday_url(@contest, @matchday)
  end

  protected

  def fetch_contest
    @contest = Contest.find(params[:contest_id])
  end
end
