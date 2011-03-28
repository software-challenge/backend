class AddingRedirectUrlToSurveyToken < ActiveRecord::Migration
  def self.up
    add_column :survey_tokens, :finished_redirect_url, :string
  end

  def self.down
    remove_column :survey_tokens, :finished_redirect_url
  end
end
