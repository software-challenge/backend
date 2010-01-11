class AddHiddenToContestants < ActiveRecord::Migration
  def self.up
    add_column :contestants, :hidden, :boolean, :default => false, :null => false
  end

  def self.down
    remove_column :contestants, :hidden
  end
end
