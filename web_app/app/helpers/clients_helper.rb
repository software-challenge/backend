require 'fileutils'
require 'zip/zip'
require 'zip/zipfilesystem'
 
module ClientsHelper
  def get_session_key_name
    ActionController::Base.session_options[:key]
  end
  
 def hash_folder(folder)
    hashes = Array.new
    Dir.entries(folder).each do |file|
      if(File.directory?(folder+"/"+file)) then
        subfolder_hashes = hash_folder(folder+"/"+file)
        subfolder_hashes.each do |hash|
          hashes[hashes.length] = hash
        end
      elsif(File.exists?(folder+"/"+file)) then 
       hashes[hashes.length] = Digest::SHA1.hexdigest(File.open(folder+"/"+file.name, "r").read)
      end
    end
    return hashes
  end

  def unzip_file (file, destination)
    Zip::ZipFile.open(file) { |zip_file|
     zip_file.each { |f|
       f_path=File.join(destination, f.name)
       FileUtils.mkdir_p(File.dirname(f_path))
       zip_file.extract(f, f_path) unless File.exist?(f_path)
      }
    }
  end
 
  def get_readable_client_name(client)
    name = "#{client.contestant.name[0..25]}  - #{client.created_at.strftime("%d.%m.%Y")}"
    name += " - "+client.name[0..15] unless client.name.nil?
    return name
  end
end
