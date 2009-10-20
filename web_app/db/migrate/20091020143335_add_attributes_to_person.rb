class AddAttributesToPerson < ActiveRecord::Migration
  def self.up
    add_column :people, :blocked, :boolean, :default => false, :null => false
    add_column :people, :showEmailToOthers, :boolean, :default => false, :null => false
    add_column :people, :firstname, :string, :default => '', :null => false
    add_column :people, :lastname, :string, :default => '', :null => false
  end

  def self.down
    remove_column :people, :blocked
    remove_column :people, :showEmailToOthers
    remove_column :people, :firstname
    remove_column :people, :lastname
  end
end
