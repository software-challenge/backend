class AddBindingsToGameDefinitions < ActiveRecord::Migration
  def self.up
    add_column :score_fragments, :fragment, :string

    add_column :scores, :game_definition, :string
    add_column :scores, :score_type, :string

    add_column :contests, :game_definition, :string
  end

  def self.down
    raise ActiveRecord::MigrationIrreversible
  end
end
