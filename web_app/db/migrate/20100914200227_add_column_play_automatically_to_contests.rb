class AddColumnPlayAutomaticallyToContests < ActiveRecord::Migration
  def self.up
    add_column :contests, :play_automatically, :boolean, :default => false, :null => false
  end

  def self.down
    remove_column :contests, :play_automatically
  end
end
