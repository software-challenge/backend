class DiffCheck < FakeCheck

  def perform 
    e_first = exec_if_necessary(fake_test.clients.first.file.path)
    e_second = exec_if_necessary(fake_test.clients.second.file.path)
    puts "Extracting finished!" 
    test_files(e_first, e_second)
    puts "Testing finished...Generating Result"
    read_result
    puts "Result generated."
  end

  def read_result
   p = File.join(RAILS_ROOT, "public", "system", "fake_test", fake_test.id.to_s+".log")
   matching_lines = 0;
   a_lines = 0;
   b_lines = 0;
   f = File.open(p,"r") do |file|
     while(a = file.gets)
       if a.include?("RESULT:")
         res = a.split(":")[1].split("|")
         a_lines+=res[0].to_i
         matching_lines+=2*res[1].to_i
         b_lines+=res[2].to_i
       end
     end
   end
  fragments << CheckResultFragment.new(:name => "Z1", :value => a_lines, :description => "Zeilen des ersten Clients")
  fragments << CheckResultFragment.new(:name => "Z2", :value => b_lines, :description => "Zeilen des zweiten Clients")
  fragments << CheckResultFragment.new(:name => "Matching", :value => matching_lines, :description => "Identische Zeilen")
  File.delete p
  end

  def recursive_unzip(file, destination)
    FileUtils.remove_dir(destination, true)
    FileUtils.mkdir_p destination
    FileUtils.cp(file,destination+"/client.zip")
    unzip_folder(destination,destination) 
  end

  def unzip_folder(folder,destination)
    d = Dir.open(folder)
    finished = false
    until finished
      finished = true
      d.each do |file|
       unless file == "." or file == ".."
        f_path = File.join(folder,file)
        #puts "Current File: #{file}"
        if file.match(/.*\.zip\Z/) or file.match(/.*\.jar\Z/) 
          unzip(f_path,destination)
          finished = false
        end
        if File.directory?(f_path)
          #puts "Handling Dir: #{file}"
          if is_ignored_folder?(file)
            #puts "Ignoring and deleting folder: #{file}"
            FileUtils.rm_rf(f_path)
          else
            unzip_folder(f_path,destination)
            finished = false
          end
        end
        if not is_comparable?(file) 
          #puts "Deleting uncomparable File: #{file}"
          File.delete(f_path)
        end
      end
     end
     decompile(destination)
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
          zip.extract(f,ef_path)
        end
      end
    end
    true
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
    #puts "FakeTest: #{id} startet Dekompilierung"
    decompile_script = File.join(RAILS_ROOT, "public", "decompile_java.sh")
    d = Dir.open(destination)
    d.each do |file|
      f_path = File.join(destination, file)
      if file.match(/.*\.class\Z/) and File.exists?(f_path)
        class_name = file.split(".class")[0]
        system decompile_script+" '"+destination+"' '"+class_name+"' '"+File.join(destination, class_name+".java")+"' '"+f_path+"'"
      end
     end
    #puts "FakeTest: "+id.to_s+" beendet Dekompilierung"
  end

  def is_comparable?(file)
    extension = file.split(".").last
    comparable_extensions = ['txt','doc','xml','c','h','java','py','rb','pyc','sh','pl','pas']
    comparable_extensions.include? extension
  end

  def test_file(a,b)
    d = File.join(RAILS_ROOT,"public")
    e = File.join(d, "system", "fake_test")
    FileUtils.makedirs(e) unless File.directory?(e)
    script = File.join(d,'fake_test.sh')
    system "sh #{script} #{a} #{b} >> #{File.join(e,id.to_s)}.log" if is_comparable?(a) and is_comparable?(b)
  end

def test_files(folder_a, folder_b)
  puts "FakeTest: "+id.to_s+" startet Test"
  a = Dir.open(folder_a)
  b = Dir.open(folder_b) 
  a.each do |af|
     b.each do |bf| 
      test_file(File.join(folder_a,af), File.join(folder_b,bf))
     end
  end
  puts "FakeTest: "+id.to_s+" beendet Test"
end 

def exec_if_necessary(file)
  exec_path = file.split("original")[0]+"fake_test"
  #unless File.exists?(exec_path)
    recursive_unzip(file, exec_path)
  #end
  return exec_path
end
 
end
