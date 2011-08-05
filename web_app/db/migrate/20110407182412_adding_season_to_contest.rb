class AddingSeasonToContest < ActiveRecord::Migration
  def self.up
    add_column :contests, :season_phase_id, :integer
  end

  def self.down
    remove_column :contests, :season_phase_id
  end
end
