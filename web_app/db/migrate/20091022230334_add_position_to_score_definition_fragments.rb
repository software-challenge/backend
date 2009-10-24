class AddPositionToScoreDefinitionFragments < ActiveRecord::Migration
  def self.up
    add_column :score_definition_fragments, :position, :integer
  end

  def self.down
    remove_column :score_definition_fragments, :position
  end
end
