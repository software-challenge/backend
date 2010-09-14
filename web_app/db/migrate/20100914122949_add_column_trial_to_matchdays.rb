class AddColumnTrialToMatchdays < ActiveRecord::Migration
  def self.up
    add_column :matchdays, :trial, :boolean, :default => false, :null => false
  end

  def self.down
    remove_column :matchdays, :trial
  end
end
