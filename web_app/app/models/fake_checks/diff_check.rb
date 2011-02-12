class DiffCheck < FakeCheck

  def perform 
    fake_test_path = File.join(RAILS_ROOT, "public", "system", "fake_test", fake_test.id.to_s)
    logfile = File.join(fake_test_path, "#{fake_test.id}.log")
    clients_path = File.join(fake_test_path, "clients")
    e_first = recursive_unzip(clients.first.file.path, File.join(clients_path, "0"), clients.first)
    e_second = recursive_unzip(clients.second.file.path, File.join(clients_path, "1"), clients.second)
    puts "Extracting finished!"
    test_files(e_first, e_second, logfile)
    puts "Testing finished...Generating Result"
    read_result(logfile)
    puts "Result generated."
    FileUtils.remove_dir(fake_test_path)
  end

 protected
  def read_result(logfile)
    matching_lines = 0
    a_lines = 0
    b_lines = 0
     if File.exists? logfile 
       f = File.open(logfile,"r") do |file|
         while(a = file.gets)
           if a.include?("RESULT:")
             res = a.split(":")[1].split("|")
             a_lines+=res[0].to_i
             matching_lines+=2*res[1].to_i
             b_lines+=res[2].to_i
           end
         end
       end
     end
    gfrag!("Z1",a_lines,"Zeilen des ersten Clients")
    gfrag!("Z2", b_lines, "Zeilen des zweiten Clients")
    gfrag!("Matching", matching_lines, "Identische Zeilen")
  end

  def recursive_unzip(file, destination, client)
    init_extraction_status
    FileUtils.remove_dir(destination, true) if File.directory?(destination)
    FileUtils.mkdir_p destination
    unless whitelist.file_whitelisted?(file)
      FileUtils.cp(file,destination+"/client.zip")
      unzip_folder(destination,destination) 
      delete_not_needed_files(destination) 
      decompile(destination)
    else
      @whitelisted_files += 1
    end
    generate_extraction_status_fragments! client
    destination
  end
  
  def init_extraction_status
   @whitelisted_files = 0
   @uncomparable_files = 0
   @decompiled_files = 0
   @ignored_folders = 0
   @extracted_archives = 0
   @extracted_files = 0
  end

  def generate_extraction_status_fragments!(client)
   cl = (client == clients.first ? "ersten" : "zweiten")
   nmbr = (client == clients.first ? "1" : "2")
   gfrag!("Whitelist Cl.#{nmbr}", @whitelisted_files, "Dateien auf der Whitelist für #{cl} Client.")
   gfrag!("Uncomparable Cl.#{nmbr}", @uncomparable_files, "Nicht vergleichbare Dateien für #{cl} Client.")
   gfrag!("Decompiled Cl.#{nmbr}", @decompiled_files, "Decompilierte .class Dateien des #{cl} Clients.")
   gfrag!("Ignored Fld Cl.#{nmbr}", @ignored_folders, "Ignorierte Order des #{cl} Clients")
   gfrag!("Ext. Arch Cl.#{nmbr}", @extracted_archives, "Entpackte Archivdateien des #{cl} Clients")
   gfrag!("Ext. Files Cl.#{nmbr}", @extracted_files, "Extrahierte Dateien des #{cl} Clients")
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
          @extracted_archives += 1
          File.delete(f_path)
          finished = false
        end
        if File.directory?(f_path)
          if is_ignored_folder?(file)
            @ignored_folders += 1
            FileUtils.rm_rf(f_path)
          else
            unzip_folder(f_path,destination)
            finished = false
          end
        end
      end
     end
   end
  end
  
      
  def unzip(file, destination)
    FileUtils.makedirs(destination) unless File.exists?(destination)
    Zip::ZipFile.open(file) do |zip|
      zip.each do |f|
        unless f.is_directory or is_in_ignored_folder?(f.name)
          split = f.name.split("/")
          f_name = split.last
          f_path = File.join(destination, f_name)
          i = 0;
          ef_path = f_path.delete("$")
          while File.exists?(ef_path) do
            ef_path = File.join(destination, i.to_s+"_"+f_name)
            i+=1
          end
          zip.extract(f,ef_path.gsub(" ", "_"))
          @extracted_files += 1
        end
      end
    end
  end
   
  # Deletes files of type we do not want to compare and whitelisted files!
  def delete_not_needed_files(dest)
    Dir.open(dest).each do |file|
      unless File.directory? file
        f_path = File.join(dest,file)
        if not is_comparable?(file) 
          @uncomparable_files += 1
          File.delete(f_path)
        elsif contest.whitelist.file_whitelisted?(f_path)
          @whitelisted_files += 1
          File.delete(f_path)
        end
      end
    end 
  end

  def is_ignored_folder?(path)
    ignored_folders = ["lib","META-INF"]
    ignored_folders.include? path.split("/").last
  end

  def is_in_ignored_folder?(path)
    ignored_folders.each do |f|
      if path.split("/").include?(f)
        return true
      end
    end
    return false
  end

  def ignored_folders 
    ["lib","META-INF"]
  end

  def decompile(destination)
    decompile_script = File.join(RAILS_ROOT, "lib", "fake_tests", "decompile_java.sh")
    d = Dir.open(destination)
    d.each do |file|
      f_path = File.join(destination, file)
      if file.match(/.*\.class\Z/) and File.exists?(f_path)
        class_name = file.split(".class")[0]
        @decompiled_files += 1
        system decompile_script+" '"+destination+"' '"+class_name+"' '"+File.join(destination, class_name+".java")+"' '"+f_path+"'"
      end
     end
  end

  def is_comparable?(file)
    extension = file.split(".").last
    comparable_extensions = ['class','txt','doc','xml','c','h','java','py','rb','pyc','sh','pl','pas']
    comparable_extensions.include? extension
  end

  def test_file(a,b,logfile)
    e = File.join(RAILS_ROOT, "public" , "system", "fake_test")
    FileUtils.makedirs(e) unless File.directory?(e)
    script = File.join(RAILS_ROOT, "lib", "fake_tests", 'fake_test.sh')
    system "sh #{script} #{a} #{b} >> #{logfile}" if is_comparable?(a) and is_comparable?(b)
  end

def test_files(folder_a, folder_b, logfile)
  puts "FakeCheck: "+id.to_s+" startet Test"
  a = Dir.open(folder_a)
  b = Dir.open(folder_b) 
  a.each do |af|
     b.each do |bf| 
      test_file(File.join(folder_a,af), File.join(folder_b,bf), logfile)
     end
  end
  puts "FakeCheck: "+id.to_s+" beendet Test"
end 
 
end
