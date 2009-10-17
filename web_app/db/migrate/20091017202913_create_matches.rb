class CreateMatches < ActiveRecord::Migration
  def self.up
    create_table :matches do |t|
      t.integer :set_id
      t.string :set_type

      t.timestamps
    end
  end

  def self.down
    drop_table :matches
  end
end
