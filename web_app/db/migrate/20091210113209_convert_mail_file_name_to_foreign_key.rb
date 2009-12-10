class ConvertMailFileNameToForeignKey < ActiveRecord::Migration
  def self.up
    add_column :clients, :main_file_entry_id, :integer
    Client.reset_column_information

    Client.all.each do |c|
      entry = c.file_entries.find_by_file_name(c.main_file_name)
      c.main_file_entry = entry
      c.save!
    end

    remove_column :clients, :main_file_name
  end

  def self.down
    add_column :clients, :main_file_name, :string
    Client.reset_column_information

    Client.all.each do |c|
      if c.main_file_entry
        c.main_file_name = c.main_file_entry.file_name
        c.save!
      end
    end

    remove_column :clients, :main_file_entry_id
  end
end
