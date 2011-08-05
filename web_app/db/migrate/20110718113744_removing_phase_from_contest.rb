class RemovingPhaseFromContest < ActiveRecord::Migration
  def self.up
    remove_column :contests, :phase
  end

  def self.down
    add_column :contests, :phase, :string
  end
end
