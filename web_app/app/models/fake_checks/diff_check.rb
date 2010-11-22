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
    while(line = file.gets)
      if a.contains("RESULT:")
        res = a.split(":")[1].split["|"]
        a_lines+=res[0]
        matching_lines+=res[1]
        b_lines+=res[2]
      end
    end
  end
  fragments << CheckResultFragment.new(:name => "Z1", :value => a_lines, :description => "Zeilen des ersten Clients")
  fragments << CheckResultFragment.new(:name => "Z2", :value => b_lines, :description => "Zeilen des zweiten Clients")
  fragments << CheckResultFragment.new(:name => "Matching", :value => matching_lines, :description => "Identische Zeilen")
  File.delete p
end

  def recursive_unzip(file, destination)
    unzip(file, destination)
    finished = false
    until finished
      d = Dir.open(destination)
      d.each do |f|
        if f.match(/.*\.zip\Z/) or f.match(/.*\.jar\Z/)
          unzip(File.join(destination, f), destination)
          File.delete(f) unless file == fake_test.clients.first.file.path or file == fake_test.clients.second.file.path
          next
        end
      end
      finished = true
    end
    decompile(destination)
  end
      
  def unzip(file, destination)
    FileUtils.makedirs(destination) unless File.exists?(destination)
    Zip::ZipFile.open(file) do |zip|
      zip.each do |f|
        unless f.is_directory 
          f_name = f.name.split("/").last
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

  def decompile(destination)
    puts "FakeTest: "+id.to_+" startet Dekompilierung"
    decompile_script = File.join(RAILS_ROOT, "public", "decompile_java")
    d = Dir.open(destination)
    d.each do |file|
      f_path = File.join(destination, file)
      if file.match(/.*\.class\Z/) and File.exists?(f_path)
        class_name = file.split(".class")[0]
        system decompile_script+" '"+destination+"' '"+class_name+"' '"+File.join(destination, class_name+".java")+"' '"+f_path+"'"
      end
     end
    puts "FakeTest: "+id.to_s+" beendet Dekompilierung"
  end

  def test_file(a,b)
    unless File.directory?(a) or File.directory?(b)
      d = File.join(RAILS_ROOT,"public")
      e = File.join(d, "system", "fake_test")
      FileUtils.makedirs(e) unless File.directory?(e)
      system "sh "+File.join(d,"fake_test.sh")+" "+a+" "+b+" >> "+ File.join(e,id.to_s+".log")
    end
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
  unless File.exists?(exec_path)
    recursive_unzip(file, exec_path)
  end
  return exec_path
end
 
end
