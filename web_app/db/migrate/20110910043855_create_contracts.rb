class CreateContracts < ActiveRecord::Migration
  def self.up
    create_table :contracts do |t|
      t.integer :person_id
      t.integer :hours, :default => 0, :null => false
      t.text :description
      t.timestamps
    end
  end

  def self.down
    drop_table :contracts
  end
end
