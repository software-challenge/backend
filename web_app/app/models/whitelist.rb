class Whitelist < ActiveRecord::Base
  PATH = File.join(RAILS_ROOT, "public", "system", "fake_test", "whitelist") # The Path where the uploaded files are saved and extracted to add them to the database!
  
  validates_presence_of :contest

  belongs_to :contest, :dependent => :destroy
  has_many :entries, :class_name => "WhitelistEntry"

  def upload_file(upload, comment = nil)
    @comment = comment 
    dir = File.join(PATH, contest.id.to_s)
    filename = upload["file"].original_filename
    path = File.join(dir,filename)
    FileUtils.mkdir_p dir
    File.open(path, "wb"){ |f| f.write(upload["file"].read) } # Read the uploaded file and save it on the disk
    add_file_to_whitelist(path)
    if filename.match(/.*\.zip\Z/) or filename.match(/.*\.jar\Z/) 
     add_zip_to_whitelist(path)
    end
    FileUtils.remove_dir(dir, true) # After adding the stuff remove all stuff
  end

  def add_zip_to_whitelist(file_path)
    extr_dest = File.join(PATH, contest.id.to_s, "extract")
    recursive_unzip(file_path, extr_dest)
    add_folder_to_whitelist(extr_dest)
  end

  def add_folder_to_whitelist(folder)
    d = Dir.open(folder)
    d.each do |file|
      f_path = File.join(folder,file)
      unless File.directory?(f_path)
        add_file_to_whitelist(f_path)
      end
    end
  end

  def add_file_to_whitelist(file_path)
    checksum = hash_file(file_path)
    unless WhitelistEntry.find_by_checksum(checksum)
      entry = WhitelistEntry.new
      entry.filename = file_path.split("/").last
      entry.comment = @comment
      entry.checksum = checksum
      entry.save!
      entries << entry
    end
  end

  def hash_file(file_path)
    Digest::SHA2.hexdigest(File.read(file_path))
  end

  def checksum_whitelisted?(checksum)
    not entries.find_by_checksum(checksum).nil? 
  end

  def file_whitelisted?(file_path)
    checksum_whitelisted?(hash_file(file_path))
  end

protected
  def recursive_unzip(file, destination)
    FileUtils.remove_dir(destination, true)
    FileUtils.mkdir_p destination
    FileUtils.cp(file,destination+"/archiv.zip")
    unzip_folder(destination, destination)
  end

  def unzip_folder(folder,destination)
    d = Dir.open(folder)
    finished = false
    until finished
      finished = true
      d.each do |file|
       unless file == "." or file == ".."
        f_path = File.join(folder,file)
        if file.match(/.*\.zip\Z/) or file.match(/.*\.jar\Z/) 
          unzip(f_path,destination)
          add_file_to_whitelist(f_path)
          FileUtils.rm(f_path)
          finished = false
        elsif File.directory?(f_path)
            unzip_folder(f_path,destination)
            FileUtils.remove_dir(f_path, true)
            finished = false
        end
       end
      end
    end
  end

  def unzip(file, destination)
    FileUtils.makedirs(destination) unless File.exists?(destination)
    Zip::ZipFile.open(file) do |zip|
      zip.each do |f|
        unless f.is_directory
          split = f.name.split("/")
          f_name = split.last
          f_path = File.join(destination, f_name)
          i = 0;
          ef_path = f_path.delete("$")
          while File.exists?(ef_path) do
            ef_path = File.join(destination, i.to_s+"_"+f_name)
            i+=1
          end
          zip.extract(f,ef_path)
        end
      end
    end
  end
  
end
