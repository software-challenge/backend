class AddColumnNameToMatchday < ActiveRecord::Migration
  def self.up
    add_column :matchdays, :name, :string, :default => ""
  end

  def self.down
    remove_column :matchdays, :name
  end
end
