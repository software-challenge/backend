class EnableMatchAndMatchSlotForInheritance < ActiveRecord::Migration
  def self.up
    add_column :matches, :type, :string
    add_column :match_slots, :type, :string
  end

  def self.down
    remove_column :matches, :type
    remove_column :match_slots, :type
  end
end
