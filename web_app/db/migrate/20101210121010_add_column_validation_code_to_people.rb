class AddColumnValidationCodeToPeople < ActiveRecord::Migration
  def self.up
    add_column :people, :validation_code, :string, :null => true
  end

  def self.down
    remove_column :people, :validation_code
  end
end
