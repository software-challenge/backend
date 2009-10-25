class AssociateMatchSlotWithMatchdaySlot < ActiveRecord::Migration
  def self.up
    remove_column :match_slots, :contestant_id, :contestant_type
    add_column :match_slots, :matchday_slot_id, :integer
  end

  def self.down
    remove_column :match_slots, :matchday_slot_id
    add_column :match_slots, :contestant_id, :integer
    add_column :match_slots, :contestant_type, :string
  end
end
