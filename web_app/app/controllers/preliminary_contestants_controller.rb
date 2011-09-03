class PreliminaryContestantsController < ApplicationController  
  before_filter :fetch_preliminary_contestant
  before_filter :fetch_school
  before_filter :handle_registration_over

  access_control do
    allow :administrator

    action :show, :create do
      allow logged_in, :if => :own_team?
    end

    action :new, :index do
      allow all
    end

    action :confirm_participation do
      allow logged_in, :if => :own_team?
    end
  end


  def fetch_preliminary_contestant
    @preliminary_contestant = PreliminaryContestant.find_by_id(params[:id])
  end

  def fetch_school
    @school = School.find_by_id(params[:school_id]) 
    @school = @preliminary_contestant.school if @preliminary_contestant 
  end

  def own_team?
    return true if administrator?
    return false unless @current_user 
    @preliminary_contestant ? @current_user == @preliminary_contestant.person : action_name == "create"
  end

  def show
    @team = @preliminary_contestant
    respond_to do |format|
      format.html {
        unless @team.person == @current_user or @current_user.has_role? :administrator 
          redirect_to new_season_school_preliminary_contestant_url(@season,@school) 
        end
      }
      format.js {
        render :partial => "form", :locals => {:team => @team}
      }
    end
  end

  def destroy
    if @preliminary_contestant.destroy
      flash[:notice] = "Voranmeldung wurde erfolgreich entfernt."
      redirect_to [@season, :preliminary_contestants]
    else
      flash[:error] = "Beim Entfernen der Voranmeldung trat ein Fehler auf"
      render :action => :show
    end
  end

  def create
    if params[:id]
      # Update
      @team = @preliminary_contestant
      if @current_user.has_role?(:creator, @team) or administrator?
        success = @team.update_attributes(params[:team])
      else 
        success = false
      end
    else
      # Create new
      @team = PreliminaryContestant.create(params[:team])
      @team.school = @school
      @team.person = @current_user
      success = @team.save
      if success
        @current_user.has_role!(:creator, @team)
        tokens = []
        if (@season.recall? or @season.validation? or @season.preparation?) and @season.recall_survey
          token = SurveyToken.new({:survey => @season.recall_survey, :token_owner => @team})
          token.finished_redirect_url = surveys_season_school_url(@season,@school)
          token.save!
          if @season.use_custom_recall_settings and @season.recall_survey_template 
            EventMailer.deliver_survey_invite_notification(@current_user, @season, @current_user.generate_login_token, [token], @season.recall_survey_template)
          else
            tokens << token
          end
        end

        if (@season.validation? or @season.preparation?) and @season.validation_survey 
          token = SurveyToken.new({:survey => @season.validation_survey, :token_owner => @team})
          token.finished_redirect_url = surveys_season_school_url(@season,@school)
          token.save!
          if @season.use_custom_recall_settings and @season.validation_survey_template
            EventMailer.deliver_custom_survey_invite_notification(@current_user, @season, @current_user.generate_login_token, [token], @season.validation_survey_template)
          else
            tokens << token
          end
        end

        unless tokens.empty?
          EventMailer.deliver_survey_invite_notification(@current_user,@season,@current_user.generate_login_token,tokens)
        end

      end
    end
    unless success
      errors = @team.errors
      if params[:id]
        @team.reload
      end
    end
    respond_to do |format|
      format.html {
        redirect_to new_season_school_preliminary_contestant_url(@season, @school)
      }
      format.js {
        render :partial => "form", :locals => {:team => @team, :errors => errors, :new_record => !params[:id]}
      }
    end
  end

  def new
    redirect_to season_url(@season) unless logged_in?
    @team = PreliminaryContestant.new
  end

  def confirm_participation
    @preliminary_contestant.participation_confirmed = true
    if @preliminary_contestant.save
      flash[:notice] = "Das Team #{@preliminary_contestant.name} wurde erfolgreich verbindlich angemeldet."
      redirect_to :back
    else
      flash[:error] = "Das Team #{@preliminary_contestant.name} konnte nicht verbindlich angemeldet werden."
    end
  end

  def index
    if administrator?
      @preliminary_contestants = @season.preliminary_contestants
    else
      @preliminary_contestants = @season.preliminary_contestants.select{|p| p.person == @current_user}
    end
    @schools_without_teams_in_states = {}; 
    @preliminary_contestants_in_states = {};
    @schools_without_teams = @season.schools.select{|s| s.preliminary_contestants.empty?}
    @schools_without_teams.each{|s| st = s.state.downcase; @schools_without_teams_in_states[st] = (@schools_without_teams_in_states[st] || 0) + 1}
    @preliminary_contestants.each{|s| st = s.school.state.downcase; @preliminary_contestants_in_states[st] = (@preliminary_contestants_in_states[st] || 0) + 1};       
    @total_count = @preliminary_contestants.count + @schools_without_teams.count
  end

  protected

  def handle_registration_over
    redirect_to @season unless @season.team_registration_allowed?
  end

end
