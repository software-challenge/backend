module SoChaManager
  require 'tempfile'
  
  class Manager
    
    IP = "127.0.0.1"
    PORT = 13050
    HUI = 'swc_2010_hase_und_igel'
    VM_WATCH_FOLDER = ENV['VM_WATCH_FOLDER']

    def initialize
      if !File.exists? VM_WATCH_FOLDER
        Dir.mkdir(VM_WATCH_FOLDER)
      elsif !File.directory? VM_WATCH_FOLDER
        raise "VM_WATCH_FOLDER (#{VM_WATCH_FOLDER}) isn't a directory"
      end
    end
    
    def connect!(ip = IP, port = PORT, game = HUI)
      @ip, @port, @game = ip, port, game
      @client = Client.new ip, port
    end

    def play(round)
      player_names = round.slots.collect(&:ingame_name)

      @client.prepare HUI, player_names do |success,response|
        begin
          if success
            puts "Game has been prepared"

            reservations = response.xpath '//reservation'
            codes = reservations.collect(&:content)

            ActiveRecord::Base.benchmark "Preparing the Client VMs" do
              round.slots.each_with_index do |slot, i|
                start_client(slot, codes[i])
              end
            end

            puts "All clients have been prepared"
          else
            puts "Couldn't prepare game!"
            @client.close
          end
        rescue => e
          puts "An error occured: #{e}"
          raise
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

    def start_client(slot, reservation)
      puts "Starting client (id=#{slot.client.id}) for '#{slot.name}'"

      file = slot.client.file.path
      executable = File.join(".", slot.client.main_file_entry.file_name)

      # clone the zip-file
      zip_file = copy_to_temp_file(file, "executable.zip")
      zip = Zip::ZipFile.open(zip_file.path)

      # attach startup.sh
      startup = attach_startup_sh(zip, "#{executable} --host 192.168.56.2 --port 12345 --reservation #{reservation}")
      zip.close
      startup.unlink

      # move zip file to WATCH folder
      move_to_watch_folder(zip_file.path, slot.ingame_name)
      zip_file.unlink
    end
    
    def move_to_watch_folder(file_path, key = "undefined")
      file_name = "#{Time.now.to_i}_#{key}_#{(rand * 1000).ceil}.zip"
      File.copy(file_path, File.join(VM_WATCH_FOLDER, file_name), true)
    end
    
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
      
      src = File.open(src_path, "rb")
      src.binmode
      
      dest = Tempfile.new(temp_id)
      dest.binmode

      src_size = File.size(src_path)
      bytes_read = 0
      chunk_size = 512.kilobytes

      puts "#{src_path} -> #{dest.path}"

      while bytes_read < src_size
        buffer = src.sysread(chunk_size)
        dest.syswrite(buffer)
        bytes_read += chunk_size
      end
      
      src.close
      dest.flush
      dest.close
      
      dest_size = File.size(dest.path)
      
      raise "FileSize does not match! was: #{dest_size}, expected: #{src_size}" unless dest_size == src_size
      
      return dest
      
    end
  end
end