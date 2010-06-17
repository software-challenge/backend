class RemoveColumnBlockedFromPeople < ActiveRecord::Migration
  def self.up
    remove_column :people, :blocked 
  end

  def self.down
    add_column :people, :blocked, :boolean, :default => false, :null => false
  end
end
