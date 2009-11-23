class CreateClientFileEntries < ActiveRecord::Migration
  def self.up
    create_table :client_file_entries do |t|
      t.integer :client_id
      t.string :file_name
      t.string :file_type
      t.integer :file_size
      t.integer :level
    end
  end

  def self.down
    drop_table :client_file_entries
  end
end
