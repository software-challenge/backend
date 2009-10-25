class CreateMatchdaySlots < ActiveRecord::Migration
  def self.up
    create_table :matchday_slots do |t|
      t.integer :client_id
      t.integer :matchday_id
      t.integer :contestant_id
      t.integer :score_id
      t.integer :position

      t.timestamps
    end
  end

  def self.down
    drop_table :matchday_slots
  end
end
