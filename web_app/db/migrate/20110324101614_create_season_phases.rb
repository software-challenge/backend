class CreateSeasonPhases < ActiveRecord::Migration
  def self.up
    create_table :season_phases do |t|
      t.boolean :public, :default => false, :null => false
      t.integer :season_id
      t.integer :position
      t.boolean :finished, :default => false, :null => false
      t.string :game_identifier
      t.timestamps
    end
  end

  def self.down
    drop_table :season_phases
  end
end
