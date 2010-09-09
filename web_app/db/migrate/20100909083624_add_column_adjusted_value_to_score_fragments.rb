class AddColumnAdjustedValueToScoreFragments < ActiveRecord::Migration
  def self.up
    add_column :score_fragments, :adjusted_value, :decimal, :precision => 63, :scale => 10
  end

  def self.down
    remove_column :score_fragments, :adjusted_value
  end
end
