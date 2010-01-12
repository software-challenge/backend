class AddHiddenToClients < ActiveRecord::Migration
  def self.up
    add_column :clients, :hidden, :boolean, :null => false, :default => false
  end

  def self.down
    remove_column :clients, :hidden
  end
end
