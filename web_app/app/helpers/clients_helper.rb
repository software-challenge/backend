require 'fileutils'
require 'zip/zip'
require 'zip/zipfilesystem'
 
module ClientsHelper
  def get_session_key_name
    ActionController::Base.session_options[:key]
  end
 
  # Links only if the boolean is true or the current_user is a admin. Else only the result of the text_method will be displayed! If the boolean the false_ext will be appended!
  def link_if_boolean(object, text_method, url, boolean_method = :hidden?, false_ext = "(gelöscht)")
    text = object.try(text_method)
    text += " "+false_ext.to_s if object.try(boolean_method)
    if (not object.try(boolean_method)) or @current_user.has_role?(:administrator) 
      link_to(text, url)   
    else
      text
    end
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
 
  def get_readable_client_name(client, time = true, id = true)
    name = "#{client.contestant.name[0..35]}"
    name += " - "+client.created_at.strftime("%d.%m.%Y %H:%M") if time
    name += " - "+client.name[0..15] unless client.name.nil?
    name += " - ID: #{client.id}" if id
    name
  end

  def client_tooltip(client)
     "Hochgeladen am: #{l client.created_at}  von #{client.author.name}"
  end
end
