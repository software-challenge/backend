class AddFiledataToClient < ActiveRecord::Migration
  def self.up
    add_column :clients, :file_file_name, :string # Original filename
    add_column :clients, :file_content_type, :string # Mime type
    add_column :clients, :file_file_size, :integer # File size in bytes
  end

  def self.down
    remove_column :clients, :file_file_name
    remove_column :clients, :file_content_type
    remove_column :clients, :file_file_size
  end
end
