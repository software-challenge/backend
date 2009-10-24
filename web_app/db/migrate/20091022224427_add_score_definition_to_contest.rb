class AddScoreDefinitionToContest < ActiveRecord::Migration
  def self.up
    add_column :contests, :match_score_definition_id, :integer
    add_column :contests, :round_score_definition_id, :integer
    add_column :contests, :rounds_per_match, :integer, :default => 1
    add_column :contests, :script_to_aggregate_rounds, :text
    add_column :contests, :script_to_aggregate_matches, :text
  end

  def self.down
    remove_column :contests, :script_to_aggregate_matches
    remove_column :contests, :script_to_aggregate_rounds
    remove_column :contests, :rounds_per_match
    remove_column :contests, :round_score_definition_id
    remove_column :contests, :match_score_definition_id
  end
end
