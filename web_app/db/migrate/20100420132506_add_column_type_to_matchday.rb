class AddColumnTypeToMatchday < ActiveRecord::Migration
  def self.up
    add_column :matchdays, :type, :string, :default => "Matchday"
  end

  def self.down
    remove_column :matchdays, :type
  end
end
