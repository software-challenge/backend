class AddColumnPhoneNumberToPeople < ActiveRecord::Migration
  def self.up
    add_column :people, :phone_number, :integer, :null => true
  end

  def self.down
    remove_column :people, :phone_number
  end
end
