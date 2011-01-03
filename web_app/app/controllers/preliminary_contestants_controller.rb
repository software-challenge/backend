class PreliminaryContestantsController < ApplicationController

  before_filter :fetch_school

  def fetch_school
    @school = School.find(params[:school_id])
  end

  def create
    if params[:id]
      # Update
      @team = PreliminaryContestant.find(params[:id])
      if @current_user.has_role?(:creator, @team) or administrator?
        success = @team.update_attributes!(params[:team])
      else 
        success = false
      end
    else
      # Create new
      @team = PreliminaryContestant.create(params[:team])
      @team.school = @school
      @current_user.has_role!(:creator, @team)
      success = @team.save!
    end
    respond_to do |format|
      format.html {
        redirect_to new_contest_school_preliminary_contestant_url(@contest, @school)
      }
      format.js {
        render :text => params
      }
    end
  end

  def new
    @team = PreliminaryContestant.new
  end

end
