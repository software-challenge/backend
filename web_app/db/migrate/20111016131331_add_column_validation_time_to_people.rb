class AddColumnValidationTimeToPeople < ActiveRecord::Migration
  def self.up
    add_column :people, :validation_time, :integer, :default => 48  
  end

  def self.down
    remove_column :people, :validation_time
  end
end
