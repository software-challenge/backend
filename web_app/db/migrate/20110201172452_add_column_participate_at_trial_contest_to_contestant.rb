class AddColumnParticipateAtTrialContestToContestant < ActiveRecord::Migration
  def self.up
    add_column :contestants, :participate_at_trial_contest, :boolean, :null => false, :default => false
  end

  def self.down
    remove_column :contestants, :participate_at_trial_contest
  end
end
