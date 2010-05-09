class AddColumnErrorMessageToScores < ActiveRecord::Migration
  def self.up
    add_column :scores, :error_message, :string, :default => ""
  end

  def self.down
    remove_column :scores, :error_message
  end
end
