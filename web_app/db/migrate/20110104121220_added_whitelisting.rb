class AddedWhitelisting < ActiveRecord::Migration
  def self.up
    create_table :whitelist_entries do |t|
      t.integer :whitelist_id
      t.string :filename
      t.string :checksum
      t.string :comment
      t.timestamps
    end

    create_table :whitelists do |t|
      t.integer :contest_id
      t.text :description
    end
    
  end

  def self.down
    drop_table :whitelists
    drop_table :whitelist_entries
  end
end
