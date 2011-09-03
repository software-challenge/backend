class AddingNewTemplatesToSeason < ActiveRecord::Migration
  def self.up
    remove_column :seasons, :recall_survey_title
    remove_column :seasons, :validation_survey_title
    remove_column :seasons, :recall_survey_template
    remove_column :seasons, :validation_survey_template
    add_column :seasons, :recall_survey_template_id, :integer
    add_column :seasons, :validation_survey_template_id, :integer
  end

  def self.down
    add_column :seasons, :recall_survey_title, :string
    add_column :seasons, :validation_survey_title, :string
    add_column :seasons, :recall_survey_template, :string
    add_column :seasons, :validation_survey_template, :string
    remove_column :seasons, :recall_survey_template_id
    remove_column :seasons, :validation_survey_template_id
  end
end
