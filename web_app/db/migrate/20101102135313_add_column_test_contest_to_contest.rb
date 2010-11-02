class AddColumnTestContestToContest < ActiveRecord::Migration
  def self.up
    add_column :contests, :trial_contest_id, :integer
  end

  def self.down
    remove_column :contests, :trial_contest_id
  end
end
