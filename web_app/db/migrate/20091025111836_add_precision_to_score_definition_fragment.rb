class AddPrecisionToScoreDefinitionFragment < ActiveRecord::Migration
  def self.up
    add_column :score_definition_fragments, :precision, :integer, :default => 0, :null => false
  end

  def self.down
    remove_column :score_definition_fragments, :precision
  end
end
