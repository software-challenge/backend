class CreateRoundSlots < ActiveRecord::Migration
  def self.up
    create_table :round_slots do |t|
      t.integer :match_slot_id
      t.integer :round_id
      t.integer :score_id

      t.timestamps
    end
  end

  def self.down
    drop_table :round_slots
  end
end
