module SoChaManager
  require 'tempfile'
  
  class Manager
    
    include Loggable

    HOST = "127.0.0.1"
    PORT = 13050
    HUI = 'swc_2010_hase_und_igel'
    VM_WATCH_FOLDER = ENV['VM_WATCH_FOLDER']

    attr_reader :last_result

    def initialize
      if !File.exists? VM_WATCH_FOLDER
        Dir.mkdir(VM_WATCH_FOLDER)
      elsif !File.directory? VM_WATCH_FOLDER
        raise "VM_WATCH_FOLDER (#{VM_WATCH_FOLDER}) isn't a directory"
      end
    end
    
    def connect!(ip = HOST, port = PORT, game = HUI)
      @host, @port, @game = ip, port, game
      @client = Client.new ip, port
    end

    def play(round)
      manager = self
      player_names = round.slots.collect(&:ingame_name)

      @client.prepare HUI, player_names do |success,response|
        begin
          if success
            puts "Game has been prepared"

            reservations = response.xpath '//reservation'
            codes = reservations.collect(&:content)
            room_id = response.attributes['roomId'].value
            
            puts "Observing the game."

            logfile_name = "#{Time.now.to_i}_log_#{round.id}.xml.gz"
            logfile = Tempfile.new(logfile_name)
            gzip_logfile = Zlib::GzipWriter.open(logfile.path)

            room_handler = ObservingRoomHandler.new gzip_logfile do |observer|
              begin
                puts "Logging done!"
                gzip_logfile.close
                logfile.close

                # add original_filename attribute for Paperclip
                logfile_handle = logfile.open
                def logfile_handle.original_filename=(x); @original_filename = x; end
                def logfile_handle.original_filename; @original_filename; end
                logfile_handle.original_filename = logfile_name

                # save replay to database
                round.replay = logfile_handle
                round.save!

                @last_result = observer.result
                manager.close
              ensure
                logfile.close unless logfile.closed?
                logfile.unlink
              end
            end

            @client.observe room_id, "swordfish" do |success,response|
              puts "ObservationRequest: #{success}"
              
              if success
                @client.register_room_handler room_id, room_handler
              end
            end

            # TODO: wait for observation response

            zip_files = []
            ActiveRecord::Base.benchmark "Preparing the Client VMs" do
              zip_files = round.slots.zip(codes).collect do |slot, code|
                start_client(slot, code)
              end
            end

            puts "All clients have been prepared"
            zip_files.each { |path| puts path }

            unless RAILS_ENV.to_s == "production"
              puts "Starting clients without VM"
              zip_files.each do |file|
                Thread.new do
                  begin
                    run_without_vm!(file)
                  rescue => e
                    logger.log_formatted_exception e
                  end
                end
              end
            end
          else
            puts "Couldn't prepare game!"
            @client.close
          end
        rescue => e
          puts "An error occured:\n#{e}\n#{e.backtrace.join("\n")}"
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

    def run_without_vm!(path)
      # make it absolute
      path = File.expand_path(path)

      # assert that we have the output directory
      output_directory = File.join(RAILS_ROOT, 'tmp', 'vmwatch_extract')
      Dir.mkdir output_directory unless File.directory? output_directory

      # create a directory to extrat the zip file
      directory = File.join(output_directory, File.basename(path))
      Dir.mkdir directory

      # extract
      puts "Extracting AI program..."
      full_output_path = File.expand_path(directory)
      `unzip #{path} -d #{full_output_path}`
      raise "failed to unzip" unless $?.exitstatus == 0

      puts "Starting AI program..."
      `sh -c "cd #{full_output_path}; ./startup.sh"`
      puts "AI program has been executed and returned (exitcode: #{$?.exitstatus})"
    end

    def start_client(slot, reservation)
      puts "Starting client (id=#{slot.client.id}) for '#{slot.name}'"

      ai_program = slot.client
      file = ai_program.file.path

      # add "./" as a prefix
      executable = ai_program.main_file_entry.file_name

      if ai_program.java?
        executable = "java -Dfile.encoding=UTF-8 -jar #{executable}"
      else
        executable = File.join(".", executable)
      end

      # clone the zip-file
      zip_file = copy_to_temp_file(file, "executable.zip")
      zip = Zip::ZipFile.open(zip_file.path)

      # attach startup.sh
      startup = attach_startup_sh(zip, "#{executable} --host #{@host} --port #{@port} --reservation #{reservation}")
      zip.close # writes startup into zip-file
      startup.unlink

      # move zip file to WATCH folder
      returning move_to_watch_folder(zip_file.path, slot.ingame_name) do
        zip_file.unlink
      end
    end
    
    def move_to_watch_folder(file_path, key = "undefined")
      file_name = "#{Time.now.to_i}_#{key}_#{(rand * 1000).ceil}.zip"
      target = File.expand_path(File.join(VM_WATCH_FOLDER, file_name))
      File.copy(file_path, target, true)
      return target
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