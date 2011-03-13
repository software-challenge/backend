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
      format.html
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
    @preliminary_contestants = @contest.preliminary_contestants
  end
end
