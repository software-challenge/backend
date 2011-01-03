class AddColumnContactFunctionToSchools < ActiveRecord::Migration
  def self.up
    add_column :schools, :contact_function, :string, :null => false
  end

  def self.down
    remove_column :schools, :contact_function
  end
end
