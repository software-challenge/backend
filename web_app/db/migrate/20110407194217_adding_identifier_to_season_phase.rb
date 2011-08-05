class AddingIdentifierToSeasonPhase < ActiveRecord::Migration
  def self.up
    add_column :season_phases, :identifier, :string
  end

  def self.down
    remove_column :season_phases, :idenitfier
  end
end
