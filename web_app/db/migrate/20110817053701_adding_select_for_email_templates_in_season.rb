class AddingSelectForEmailTemplatesInSeason < ActiveRecord::Migration
  def self.up
    add_column :seasons, :recall_survey_title, :string
    add_column :seasons, :validation_survey_title, :string
    add_column :seasons, :recall_survey_template, :string
    add_column :seasons, :validation_survey_template, :string
    add_column :seasons, :use_custom_recall_settings, :boolean, :default => false, :null => false
    add_column :seasons, :use_custom_validation_settings, :boolean, :default => false, :null => false
  end

  def self.down
    remove_column :seasons, :recall_survey_title
    remove_column :seasons, :validation_survey_title
    remove_column :seasons, :recall_survey_template
    remove_column :seasons, :validation_survey_template
    remove_column :seasons, :use_custom_recall_settings
    remove_column :seasons, :use_custom_validation_settings
  end
end
