class ClientFileEntry < ActiveRecord::Base
  belongs_to :client
  
  validates_presence_of :client, :file_name, :file_size, :file_type
  validates_inclusion_of :file_type, :in => %w{file directory}
  validates_numericality_of :file_size, :greater_than_or_equal_to => 0
  validates_uniqueness_of :file_name, :scope => :client_id

  named_scope(:with_level, lambda do |level|
      {:conditions => ["client_file_entries.level = ?", level]}
    end)

  named_scope(:with_file_name, lambda do |file_name|
      {:conditions => ["client_file_entries.file_name = ?", file_name]}
    end)
  
  named_scope(:descendant_of, lambda do |path|
      {:conditions => ["LOCATE(?, client_file_entries.file_name) <> 0", path]}
    end)

  named_scope :directories, :conditions => { :file_type => "directory" }

  named_scope :file_ordering, :order => "client_file_entries.file_type = 'directory' DESC, client_file_entries.file_name ASC"

  def children
    client.file_entries.with_level(level + 1).descendant_of(file_name)
  end

  def parent
    return nil if level.zero?
    client.file_entries.directories.with_level(level - 1).with_file_name(parent_directory_name).first
  end

  def directory?
    file_type == "directory"
  end

  def current?
    client.main_file_entry == self
  end

  def clean_file_name
    File.basename(file_name)
  end

  def mkdirs!
    return if level.zero?

    unless parent
      puts parent_directory_name
      client.file_entries.create!(:file_type => "directory",
        :file_name => parent_directory_name,
        :file_size => 0,
        :level => ClientFileEntry.calculate_level(parent_directory_name))
    end
  end

  def self.calculate_level(filename)
    n = 0
    filename.scan(/\/\b/) { n += 1 }
    n
  end

  # callback
  def after_create
    mkdirs!
  end

  protected

  def parent_directory_name
    return nil if level.zero?

    parts = file_name.split(/\//)
    parts.pop
    parts.push ""

    parts.join('/')
  end
end
