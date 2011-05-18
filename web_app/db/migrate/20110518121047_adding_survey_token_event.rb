class AddingSurveyTokenEvent < ActiveRecord::Migration
  def self.up
    add_column :email_events, :rcv_survey_token_notification, :boolean, :default => true,  :null => false
  end

  def self.down
    remove_column :email_events, :rcv_survey_token_notification
  end
end
