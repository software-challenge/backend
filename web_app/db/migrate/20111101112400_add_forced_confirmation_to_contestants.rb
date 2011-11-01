class AddForcedConfirmationToContestants < ActiveRecord::Migration
  def self.up
    add_column :preliminary_contestants, "forced_confirmation", :boolean, :null => false, :default => false
  end

  def self.down
    remove_column :preliminary_contestants, "forced_confirmation"
  end
end
