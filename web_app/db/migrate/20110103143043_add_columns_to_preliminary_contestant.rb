class AddColumnsToPreliminaryContestant < ActiveRecord::Migration
  def self.up
    add_column :preliminary_contestants, :participation_probability, :string, :null => false
  end

  def self.down
    remove_column :preliminary_contestants, :participation_probability
  end
end
