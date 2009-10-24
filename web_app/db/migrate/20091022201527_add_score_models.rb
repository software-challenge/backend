class AddScoreModels < ActiveRecord::Migration
  def self.up
    create_table :score_definitions do |t|
      t.timestamps
    end

    create_table :score_definition_fragments do |t|
      t.string :name
      t.integer :definition_id
      t.boolean :main, :default => false, :null => false
    end
    
    create_table :scores do |t|
      t.integer :definition_id
      t.timestamps
    end

    create_table :score_fragments do |t|
      t.integer :definition_id
      t.integer :score_id
      t.integer :value
    end

    add_column :match_slots, :score_id, :integer
    add_column :contestants, :score_id, :integer
  end

  def self.down
    drop_table :score_definitions
    drop_table :score_definition_fragments
    drop_table :scores
    drop_table :score_fragments

    remove_column :match_slots, :score_id
    remove_column :contestants, :score_id
  end
end
