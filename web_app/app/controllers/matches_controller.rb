class MatchesController < ApplicationController
 
  before_filter :fetch_parents
  before_filter :fetch_context

  access_control do
    allow :administrator

    action :show, :index_for_contestant do
      allow all
    end
  end

  def index
    @parent = Match.all

    respond_to do |format|
      format.html { redirect_to contest_matchday_url(@contest, @matchday, :anchor => "results")}
      format.xml  { render :xml => @matches }
    end
  end

  def show
    #@match = @parent.matches.find(params[:id])
    @match = Match.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @match }
    end
  end

  def reset
    @match = Match.find(params[:id])
    
    if @match.played?
      Match.transaction do
        Match.benchmark("resetting match", Logger::DEBUG, false) do
          @match.reset! true
          @match.reload
        end
      end
      flash[:notice] = "Spiel wurde zurückgesetzt!"
    end
    redirect_to contest_matchday_url(@contest, @match.matchday)
  end 

  def set_review
   @match = Match.find_by_id(params[:id])
   @description = params[:description] ? params[:description] : nil
   Review.create(:reviewable => @match) if @match.review.nil?
   @match.reload

   if params[:description] 
     @match.review.description = params[:description] 
     @match.review.save!
   end

   if params[:verified] == "true" 
     @match.review.finished!
    else
     @match.review.unfinished!
   end

   respond_to do |format|
    format.html { redirect_to :action => :show }
    format.js { render  :text => {:reviewed => @match.reviewed?}.to_json}
   end
  end

  def play
    @match = Match.find_by_id(params[:id])
    @match.perform_delayed! if !@match.played? and !@match.running?
    redirect_to :action => :show
  end

  protected

  def fetch_parents
   # if params[:contest_id]
     # @contest = Contest.find(params[:contest_id])
    #  @matchday = @contest.matchdays.find(params[:matchday_id])
     # @parent = @matchday
    if params[:contestant_id]
      @contestant = Contestant.find(params[:contestant_id])
      @parent = @contestant
    elsif params[:matchday_id]
      @matchday = Matchday.find(params[:matchday_id])
      @parent = @matchday
    else
      raise ActiveRecord::RecordNotFound
    end
  end

  def fetch_context
    @context = @contest ? @contest : @season
  end
end
