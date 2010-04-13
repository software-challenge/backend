class AddColumnManuallyDisqualifiedToRoundSlots < ActiveRecord::Migration
  def self.up
    add_column :round_slots, :qualification_changed, :boolean, :null => false, :default => false
  end

  def self.down
    remove_column :round_slots, :qualification_changed
  end
end
