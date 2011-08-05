class CreateSeasons < ActiveRecord::Migration
  def self.up
    create_table :seasons do |t|
      t.string :name
      t.string :subdomain
      t.string :state
      t.string :current_phase_id
      t.string :season_definition
      t.string :game_identifier
      t.boolean :public, :default => false, :null => false
      t.timestamps
    end
  end

  def self.down
    drop_table :seasons
  end
end
