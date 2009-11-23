class Client < ActiveRecord::Base
  belongs_to :contestant
  belongs_to :author, :class_name => "Person"
  has_many :file_entries, :class_name => "ClientFileEntry"

  has_attached_file :file

  validates_presence_of :file
  validates_attachment_presence :file
  validates_attachment_content_type :file, :content_type => 'application/octet-stream'

  validates_presence_of :author
  validates_presence_of :contestant

  def build_index!
    Client.transaction do
      Zip::ZipFile.foreach(file.path) do |e|
        file_name = e.name.to_s

        file_entries.create!(:file_type => e.ftype.to_s,
          :file_name => file_name,
          :file_size => e.size,
          :level => calculate_level(file_name))
      end
    end
  end

  protected

  def calculate_level(filename)
    n = 0
    filename.scan(/\/\b/) { n += 1 }
    n
  end
end
