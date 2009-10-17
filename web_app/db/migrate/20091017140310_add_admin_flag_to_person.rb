class AddAdminFlagToPerson < ActiveRecord::Migration
  def self.up
    add_column :people, :administrator, :boolean, :default => false, :null => false
  end

  def self.down
    remove_column :people, :administrator
  end
end
