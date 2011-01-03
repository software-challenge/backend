class CreateSchools < ActiveRecord::Migration
  def self.up
    create_table :schools do |t|
      t.string :name, :null => false
      t.integer :zipcode, :null => false
      t.string :location, :null => false
      t.string :state, :null => false
      t.integer :estimated_team_count, :null => false
      t.integer :person_id
      t.integer :contest_id
      t.timestamps
    end
  end

  def self.down
    drop_table :schools
  end
end
