class AddPositionsToSlots < ActiveRecord::Migration
  def self.up
    rename_column :match_slots, :order, :position
    add_column :round_slots, :position, :integer
  end

  def self.down
    rename_column :match_slots, :position, :order
    remove_column :round_slots, :position
  end
end
