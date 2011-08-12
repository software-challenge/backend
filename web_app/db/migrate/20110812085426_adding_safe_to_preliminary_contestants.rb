class AddingSafeToPreliminaryContestants < ActiveRecord::Migration
  def self.up
    add_column :preliminary_contestants, :participation_confirmed, :boolean, :default => false, :null => false
  end

  def self.down
    remove_column :preliminary_contestants, :participation_confirmed
  end
end
