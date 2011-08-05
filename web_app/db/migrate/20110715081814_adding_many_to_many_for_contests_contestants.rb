class AddingManyToManyForContestsContestants < ActiveRecord::Migration
  def self.up
    Contestant.transaction do 
      create_table :contestants_contests, :id => false do |t|
        t.references :contestant, :null => false
        t.references :contest, :null => false
      end
      add_index :contestants_contests, [:contestant_id, :contest_id], :unique => true
      Contestant.all.each do |c|
        unless c.contest_id.nil? 
          c.contests << Contest.find_by_id(c.contest_id)
          c.save!
        end
      end
      remove_column :contestants, :contest_id
    end
  end

  def self.down
    Contestant.transaction do 
      add_column :contestants, :contest_id, :integer
      Contestant.all.each do |c|
        unless c.contests.empty?
          c.contest_id = c.contests.first.id
          c.save!
        end
      end
      remove_index :contestants_contests, [:contestant_id, :contest_id]
      drop_table :contestants_contests
    end
  end
end
