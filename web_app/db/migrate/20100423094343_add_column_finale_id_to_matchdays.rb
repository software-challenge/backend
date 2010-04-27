class AddColumnFinaleIdToMatchdays < ActiveRecord::Migration
  def self.up
    add_column :matchdays, :finale_id, :integer
  end

  def self.down
    remove_column :matchdays, :finale_id
  end
end
