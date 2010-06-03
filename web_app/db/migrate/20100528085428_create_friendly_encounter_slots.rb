class CreateFriendlyEncounterSlots < ActiveRecord::Migration
  def self.up
    create_table :friendly_encounter_slots do |t|
      t.integer :friendly_encounter_id
      t.integer :client_id
      t.integer :contestant_id
      t.integer :score_id
      t.boolean :hidden
      t.timestamps
    end
  end

  def self.down
    drop_table :friendly_encounter_slots
  end
end
