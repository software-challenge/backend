class CreateMatchdays < ActiveRecord::Migration
  def self.up
    create_table :matchdays do |t|
      t.integer :contest_id
      t.date :when
      t.integer :order
      t.boolean :played

      t.timestamps
    end
  end

  def self.down
    drop_table :matchdays
  end
end
