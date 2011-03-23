class AddingFistPlayedAtToMatchdays < ActiveRecord::Migration
  def self.up 
    add_column :matchdays, :first_played_at, :timestamp
  end

  def self.down
    remove_column :matchdays, :first_played_at
  end
end
