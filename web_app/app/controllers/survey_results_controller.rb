class SurveyResultsController < ApplicationController
  
  access_control do 
   default :deny
   allow :administrator
  end

  unloadable
  helper 'surveyor'

  def index
    @surveys = Survey.all     
  end

  def show
    @survey = Survey.find_by_id(params[:id])
    @response_sets = @survey.response_sets
    @questions = @survey.sections_with_questions.map(&:questions).flatten
    respond_to do |format|
      format.html
      format.csv 
    end
  end
  
  def show_response
    @survey = Survey.find_by_id(params[:id])
    @survey_token = SurveyToken.find_by_id(params[:survey_token]) 
    @response_set = @survey_token.response_set if @survey_token
    @questions = @survey.sections_with_questions.map(&:questions).flatten

    unless @response_set and @survey_token
      flash[:notice] = "Eine Teilnahme fand noch nicht statt!"
      redirect_to contest_survey_tokens_url(@contest||Contest.public.last)
    end
  end
end

