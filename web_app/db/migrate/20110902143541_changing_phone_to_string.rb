class ChangingPhoneToString < ActiveRecord::Migration
  def self.up
    change_column :people, :phone_number, :string
  end

  def self.down
    change_column :people, :phone_number, :integer
  end
end
