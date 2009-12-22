class RemoveObsoleteAttributesDueToGameDefinitionChanges < ActiveRecord::Migration
  def self.up
    drop_table :score_definitions
    drop_table :score_definition_fragments

    remove_column :score_fragments, :definition_id
    remove_column :scores, :definition_id

    remove_column :contests, :round_score_definition_id
    remove_column :contests, :match_score_definition_id
    remove_column :contests, :rounds_per_match
    remove_column :contests, :script_to_aggregate_rounds
    remove_column :contests, :script_to_aggregate_matches
  end

  def self.down
    raise ActiveRecord::MigrationIrreversible
  end
end
