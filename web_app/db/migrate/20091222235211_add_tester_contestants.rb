class AddTesterContestants < ActiveRecord::Migration
  def self.up
    add_column :contestants, :tester, :boolean, :null => false, :default => false
  end

  def self.down
    remove_column :contestants, :tester
  end
end
