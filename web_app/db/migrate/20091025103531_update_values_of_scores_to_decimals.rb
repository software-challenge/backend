class UpdateValuesOfScoresToDecimals < ActiveRecord::Migration
  def self.up
    remove_column :score_fragments, :value

    # 63 is the maximum supported by many RDBMS
    add_column :score_fragments, :value, :decimal, :precision => 63, :scale => 10, :null => true
    add_column :score_definition_fragments, :example_value, :decimal, :precision => 63, :scale => 10, :null => true
  end

  def self.down
    remove_column :score_definition_fragments, :example_value
    remove_column :score_fragments, :value

    add_column :score_fragments, :value, :integer
  end
end
