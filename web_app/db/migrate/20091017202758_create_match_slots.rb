class CreateMatchSlots < ActiveRecord::Migration
  def self.up
    create_table :match_slots do |t|
      t.string :contestant_type
      t.integer :contestant_id
      t.integer :match_id
      t.integer :order

      t.timestamps
    end
  end

  def self.down
    drop_table :match_slots
  end
end
