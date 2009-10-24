class RenameMatchdayOrderToPosition < ActiveRecord::Migration
  def self.up
    rename_column :matchdays, :order, :position
  end

  def self.down
    rename_column :matchdays, :position, :order
  end
end
