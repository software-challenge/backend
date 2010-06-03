class CreateFriendlyEncounters < ActiveRecord::Migration
  def self.up
    create_table :friendly_encounters do |t|
      t.integer :contest_id
      t.datetime :played_at
      t.integer :job_id
      t.string :type, :default => "FriendlyEncounter"
      t.integer :open_for_id
      t.boolean :rejected, :default => false
      t.timestamps
    end
  end

  def self.down
    drop_table :friendly_encounters
  end
end
