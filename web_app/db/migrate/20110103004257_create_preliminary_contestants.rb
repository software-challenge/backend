class CreatePreliminaryContestants < ActiveRecord::Migration
  def self.up
    create_table :preliminary_contestants do |t|
      t.integer :school_id
      t.string :name
      t.timestamps
    end
  end

  def self.down
    drop_table :preliminary_contestants
  end
end
