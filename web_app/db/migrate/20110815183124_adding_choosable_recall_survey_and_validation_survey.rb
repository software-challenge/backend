class AddingChoosableRecallSurveyAndValidationSurvey < ActiveRecord::Migration
  def self.up
    add_column :seasons, :recall_survey_code, :string
    add_column :seasons, :validation_survey_code, :string
  end

  def self.down
    remove_column :seasons, :recall_survey_code
    remove_column :seasons, :validation_survey_code
  end
end
