class AddColumnAllowTrialRegistrationToContest < ActiveRecord::Migration
  def self.up
    add_column :contests, :allow_trial_registration, :boolean, :null => false, :default => false
  end

  def self.down
    remove_column :contests, :allow_trial_registration
  end
end
