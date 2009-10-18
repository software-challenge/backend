class MatchdaysController < ApplicationController
  before_filter :fetch_contest

  def index
    @matchdays = @contest.matchdays

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @matchdays }
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

  def play
    @matchday = @contest.matchdays.find(params[:id])

    # TODO: start a deferred job
    @matchday.played = true
    @matchday.save!

    redirect_to contest_matchday_url(@contest, @matchday)
  end

  protected

  def fetch_contest
    @contest = Contest.find(params[:contest_id])
  end
end
