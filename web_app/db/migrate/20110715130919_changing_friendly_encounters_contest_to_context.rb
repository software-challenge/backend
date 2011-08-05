class ChangingFriendlyEncountersContestToContext < ActiveRecord::Migration
  def self.up
    rename_column :friendly_encounters, :contest_id, :context_id
    add_column :friendly_encounters, :context_type, :string
    for enc in FriendlyEncounter.all do 
      enc.context_type = "Contest"
      enc.save
    end
  end

  def self.down
    rename_column :friendly_encounters, :context_id, :contest_id
    remove_column :friendly_encounters, :context_type
  end
end
