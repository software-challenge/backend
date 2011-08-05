class AddingSeasonToContestant < ActiveRecord::Migration
  def self.up
    add_column :contestants, :season_id, :integer
  end

  def self.down
    remove_column :contestants, :season_id
  end
end
