class AddColumnParametersToClients < ActiveRecord::Migration
  def self.up
    add_column :clients, :parameters, :string
  end

  def self.down
    remove_column :clients, :parameters
  end
end
