class SeasonsController < ApplicationController

  access_control do
    allow :administrator

    actions :index do
      allow all
    end

    actions :contests, :show do
      allow all, :if => :public?
    end
  end

  before_filter :fetch_season

  
  def index
    @seasons = administrator? ? Season.all : Season.public
  end

  def status
    @contest = @season.contests.first || Contest.last
  end

  def show
  end

  def phases 
    @contest = @season.contests.first || Contest.last
  end

  def contests
    redirect_to @season unless @season.contests_visible?
    @contests = administrator? ? @season.contests : @season.contests.public
  end

  def next_step
    @season.next_step!
    redirect_to :action => :status
  end

  def prev_step
    @season.prev_step!
    redirect_to :action => :status
  end

  def new
    @season = Season.new
  end

  def edit
   
  end

  def destroy
    @season and @season.destroy
    redirect_to :action => :index
  end

  def update
    if @season.update_attributes(params[:season])
      flash[:notice] = "Saison wurde erfolgreich bearbeitet."
      redirect_to :action => :show
    else
      flash[:error] = "Beim Bearbeiten der Saison trat ein Fehler auf."
      render :action => :edit
    end
  end

  def create
     @season = Season.create(params[:season])
     if @season.save
        flash[:notice] = "Saison wurde erfolgreich bearbeitet."
        redirect_to @season
     else
        flash[:error] = "Beim Bearbeiten der Saison trat ein Fehler auf."
        render :action => :new
     end
  end

  def edit_teams
    @preliminary_contestants = @season.preliminary_contestants.participation_confirmed.select{|p| p.contestant.nil?}
    flash[:notice] = "In der Spalte der Voranmeldungen werden aktuell nur die verbindlichen Voranmeldungen betrachtet! Kann aber geändert werden."
    @contestants = @season.contestants
  end

  def update_team
    @preliminary_contestant = @prelim = PreliminaryContestant.find_by_id(params[:preliminary_contestant])
    if @prelim.contestant
      contestant = @prelim.contestant
    else
      contestant = Contestant.new(:season => @season, :location => @prelim.school.location)
    end
    contestant.name = params[:contestant][:name]
    contestant.ranking = params[:contestant][:ranking]
    contestant.save
    unless @prelim.contestant 
      @prelim.person.has_role!(params[:contestant][:creator_role], contestant)
      Membership.create(:person => @prelim.person, :contestant => contestant)
      @prelim.contestant = contestant
      @prelim.save!
    end
    contestant.destroy if params[:contestant][:delete] == "true" 
    render :json => (contestant.valid? ? ["ok",contestant.id].to_json : ["error",{:errors => contestant.errors.full_messages}].to_json)
    
  end

  def new_team
    @preliminary_contestant = PreliminaryContestant.find_by_id(params[:preliminary_contestant])
    respond_to do |format|
      format.js
    end
  end

  def edit_team
     @preliminary_contestant = PreliminaryContestant.find_by_contestant_id(params[:contestant])
    respond_to do |format|
      format.js
    end 
  end

  private

  def fetch_season
    @season = Season.find_by_id(params[:id]) || Season.find_by_subdomain(params[:id]) 
    if @season and not (administrator? or @season.public)
      @season = nil
    end
  end

  def public?
    @season and @season.public
  end
end
