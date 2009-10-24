class CreateRounds < ActiveRecord::Migration
  def self.up
    create_table :rounds do |t|
      t.integer :match_id
      t.datetime :played_at

      t.timestamps
    end
  end

  def self.down
    drop_table :rounds
  end
end
