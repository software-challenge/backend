class AddClientToMatchSlot < ActiveRecord::Migration
  def self.up
    add_column :match_slots, :client_id, :integer
  end

  def self.down
    remove_column :match_slots, :client_id
  end
end
