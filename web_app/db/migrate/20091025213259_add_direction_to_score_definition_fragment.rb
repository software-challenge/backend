class AddDirectionToScoreDefinitionFragment < ActiveRecord::Migration
  def self.up
    add_column :score_definition_fragments, :direction, :string
    execute "UPDATE score_definition_fragments SET direction = 'desc' WHERE 1"
  end

  def self.down
    remove_column :score_definition_fragments, :direction
  end
end
