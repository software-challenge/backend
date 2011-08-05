class AddContestantToPreliminaryContestant < ActiveRecord::Migration
  def self.up
    add_column :preliminary_contestants, :contestant_id, :integer
  end

  def self.down
    remove_column :preliminary_contestants, :contestant_id
  end
end
