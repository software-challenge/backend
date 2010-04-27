class RemoveDeprecatedAndAddColumnPublishedToFinales < ActiveRecord::Migration
  def self.up
    remove_column :finales, :quarter_final_id
    remove_column :finales, :half_final_id
    remove_column :finales, :small_final_id
    remove_column :finales, :final_id
    add_column :finales, :published, :boolean, :default => false
  end

  def self.down
    add_column :finales, :quarter_final_id, :integer
    add_column :finales, :half_final_id, :integer
    add_column :finales, :small_final_id, :integer
    add_column :finales, :final_id, :integer
    remove_column :finales, :published
  end
end
