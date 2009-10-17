class CreateClients < ActiveRecord::Migration
  def self.up
    create_table :clients do |t|
      t.string :name
      t.integer :contestant_id
      t.integer :author_id

      t.timestamps
    end
  end

  def self.down
    drop_table :clients
  end
end
