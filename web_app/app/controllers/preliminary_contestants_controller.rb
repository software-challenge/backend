class PreliminaryContestantsController < ApplicationController
 
  before_filter :fetch_preliminary_contestant
  before_filter :fetch_school

  def fetch_preliminary_contestant
    @preliminary_contestant = PreliminaryContestant.find_by_id(params[:id])
  end

  def fetch_school
    @school = School.find_by_id(params[:school_id]) 
    @school = @preliminary_contestant.school if @preliminary_contestant 
  end

  def show
    @team = @preliminary_contestant
    respond_to do |format|
      format.html {
        unless @team.person == @current_user or @current_user.has_role? :administrator 
          redirect_to new_contest_school_preliminary_contestant_url(@contest,@school) 
        end
      }
      format.js {
        render :partial => "form", :locals => {:team => @team}
      }
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
        if @contest.phase == "recall" and @contest.recall_survey
          token = SurveyToken.new({:survey => @contest.recall_survey, :token_owner => @team})
          token.finished_redirect_url = surveys_contest_school_url(@contest,@school)
          if token.save
            EventMailer.deliver_survey_invite_notification(@current_user,@contest,@current_user.generate_login_token,[token])
          end
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
        redirect_to new_contest_school_preliminary_contestant_url(@contest, @school)
      }
      format.js {
        render :partial => "form", :locals => {:team => @team, :errors => errors, :new_record => !params[:id]}
      }
    end
  end

  def new
    redirect_to contest_url(@contest) unless logged_in?
    @team = PreliminaryContestant.new
  end
  
  def index
    if administrator?
      @preliminary_contestants = @contest.preliminary_contestants
    else
      @preliminary_contestants = @contest.preliminary_contestants.select{|p| p.person == @current_user}
    end
    @schools_without_teams_in_states = {}; 
    @preliminary_contestants_in_states = {};
    @schools_without_teams = @contest.schools.select{|s| s.preliminary_contestants.empty?}
    @schools_without_teams.each{|s| st = s.state.downcase; @schools_without_teams_in_states[st] = (@schools_without_teams_in_states[st] || 0) + 1}
    @preliminary_contestants.each{|s| st = s.school.state.downcase; @preliminary_contestants_in_states[st] = (@preliminary_contestants_in_states[st] || 0) + 1};       
    @total_count = @preliminary_contestants.count + @schools_without_teams.count
  end
end
