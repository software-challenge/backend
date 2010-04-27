class AddColumnSmallFinaleIdToFinales < ActiveRecord::Migration
  def self.up
    add_column :finales, :small_final_id, :integer
  end

  def self.down
    remove_column :finales, :small_final_id
  end
end
