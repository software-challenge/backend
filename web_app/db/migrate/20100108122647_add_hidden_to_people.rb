class AddHiddenToPeople < ActiveRecord::Migration
  def self.up
    add_column :people, "hidden", :boolean, :null => false, :default => false
  end

  def self.down
    remove_column :people, "hidden"
  end
end
