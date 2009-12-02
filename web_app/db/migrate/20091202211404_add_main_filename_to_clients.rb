class AddMainFilenameToClients < ActiveRecord::Migration
  def self.up
    add_column :clients, :main_file_name, :string
  end

  def self.down
    remove_column(:clients, :main_file_name)
  end
end
