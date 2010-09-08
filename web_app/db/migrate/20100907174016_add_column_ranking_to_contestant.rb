class AddColumnRankingToContestant < ActiveRecord::Migration
  def self.up
    add_column :contestants, :ranking, :string, :null => false, :default => "none"
  end

  def self.down
    remove_column :contestants, :ranking
  end
end
