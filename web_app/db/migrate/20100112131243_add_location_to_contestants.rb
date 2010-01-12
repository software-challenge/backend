class AddLocationToContestants < ActiveRecord::Migration
  def self.up
    add_column :contestants, :location, :string, :null => false, :default => ""
  end

  def self.down
    remove_column :contestants, :location
  end
end
