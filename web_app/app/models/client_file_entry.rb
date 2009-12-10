class ClientFileEntry < ActiveRecord::Base
  belongs_to :client
  
  validates_presence_of :client, :file_name, :file_size, :file_type

  named_scope(:with_level, lambda do |level|
      {:conditions => ["client_file_entries.level = ?", level]}
    end)
  
  named_scope(:descendant_of, lambda do |path|
      {:conditions => ["LOCATE(?, client_file_entries.file_name) <> 0", path]}
    end)

  def children
    client.file_entries.with_level(level + 1).descendant_of(file_name)
  end

  def directory?
    file_type == "directory"
  end

  def current?
    file_name == client.main_file_name
  end
end
