class CreateFinales < ActiveRecord::Migration
  def self.up
    create_table :finales do |t|
      t.integer :quarter_final_id
      t.integer :half_final_id
      t.integer :final_id 
      t.timestamps
    end
  end

  def self.down
    drop_table :finales
  end
end
