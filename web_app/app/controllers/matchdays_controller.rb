class MatchdaysController < ApplicationController
  before_filter :fetch_contest

  # GET /matchdays
  # GET /matchdays.xml
  def index
    @matchdays = @contest.matchdays

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @matchdays }
    end
  end

  # GET /matchdays/1
  # GET /matchdays/1.xml
  def show
    @matchday = @contest.matchdays.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @matchday }
    end
  end

  # PUT /matchdays/1
  # PUT /matchdays/1.xml
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

  # DELETE /matchdays/1
  # DELETE /matchdays/1.xml
  def destroy
    @matchday = @contest.matchdays.find(params[:id])
    @matchday.destroy

    respond_to do |format|
      format.html { redirect_to(matchdays_url) }
      format.xml  { head :ok }
    end
  end

  protected

  def fetch_contest
    @contest = Contest.find(params[:contest_id])
  end
end
