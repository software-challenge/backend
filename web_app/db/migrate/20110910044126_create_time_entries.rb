class CreateTimeEntries < ActiveRecord::Migration
  def self.up
    create_table :time_entries do |t|
      t.integer :minutes, :default => 0, :null => false
      t.string :title
      t.text :description
      t.integer :person_id
      t.string :context_type
      t.integer :context_id
      t.timestamps
    end
  end

  def self.down
    drop_table :time_entries
  end
end
