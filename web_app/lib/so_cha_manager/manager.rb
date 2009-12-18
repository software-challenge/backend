module SoChaManager
  require 'tempfile'
  
  class Manager
    
    IP = "127.0.0.1"
    PORT = 13050
    HUI = 'swc_2010_hase_und_igel'

    def initialize
      # nothing to do
    end
    
    def connect!(ip = IP, port = PORT, game = HUI)
      @ip, @port, @game = ip, port, game
      @client = Client.new ip, port
    end

    def play(players = [])
      player_names = players.collect &:first

      @client.prepare HUI, player_names do |success,response|
        if success
          reservations = response.xpath '//reservation'
          codes = reservations.collect &:content
          
          players.each_with_index do |data, i|
            player_name, file, executable = *data
            reservation = codes[i]
            
            # clone the zip-file
            zip_file = copy_to_temp_file(file, "executable.zip")            
            zip = Zip::ZipFile.open(zip_file.path)
            
            # attach startup.sh
            startup = attach_startup_sh(zip, "#{executable} --host 192.168.56.2 --port 12345 --reservation #{reservation}")
            zip.close
            startup.unlink
            
            # move zip file to WATCH folder
            File.copy(zip_file.path, "./tmp/#{Time.now.to_i}_#{player_name}_#{(rand * 1000).ceil}.zip", true)
            zip_file.unlink
          end
        else
          puts "Couldn't prepare game!"
          @client.close
        end
      end
    end

    def done?
      @client.done?
    end
    
    def close
      @client.close
    end
    
    protected
    
    # Attaches the required startup.sh file to the
    # +zip+ file and return the file-handle.
    def attach_startup_sh(zip, cmd_line)
      startup = Tempfile.new("startup.sh")
      startup.puts("#!/bin/bash")
      startup.puts(cmd_line)
      startup.flush
      startup.close
      zip.add("startup.sh", startup.path)
      startup
    end
    
    # creates a Tempfile and writes the contents
    # of the source file to it. Then returns the
    # created Tempfile
    def copy_to_temp_file(src_path, temp_id = "tmp")
      
      raise "file does not exist" unless File.exist?(src_path)
      
      src_size = File.size(src_path)
      
      src = File.open(src_path, "rb")
      src.binmode
      
      dest = Tempfile.new(temp_id)
      dest.binmode
      
      bytes_read = 0
      chunk_size = 100
      
      while bytes_read < src_size
        buffer = src.sysread(chunk_size)
        dest.syswrite(buffer)
        bytes_read += chunk_size
      end
      
      src.close
      dest.flush
      dest.close
      
      dest_size = File.size(dest.path)
      
      raise "FileSize does not match: #{out_size} #{size}" unless dest_size == src_size
      
      return dest
      
    end
  end
end