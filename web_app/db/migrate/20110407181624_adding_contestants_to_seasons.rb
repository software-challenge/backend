class AddingContestantsToSeasons < ActiveRecord::Migration
  def self.up
   create_table :contestants_seasons, :id => false do |t|
     t.integer :contestant_id
     t.integer :season_id
   end

   create_table :contestants_contests, :id => false do |t|
     t.integer :contestant_id
     t.integer :contest_id
   end

  end

  def self.down
    drop_table :contestants_seasons
    drop_table :contestants_contests
  end
end
