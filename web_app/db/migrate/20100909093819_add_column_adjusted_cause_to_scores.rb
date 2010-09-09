class AddColumnAdjustedCauseToScores < ActiveRecord::Migration
  def self.up
    add_column :scores, :adjusted_cause, :string
  end

  def self.down
    remove_column :scores, :adjusted_cause
  end
end
