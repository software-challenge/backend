module SoChaManager
  module Executor
    def play(round)
      manager = self
      player_names = round.slots.collect(&:ingame_name)

      @client.prepare round.game_definition.plugin_guid, player_names do |success,response|
        begin
          if success
            logger.info "Game has been prepared"

            reservations = response.xpath '//reservation'
            codes = reservations.collect(&:content)
            room_id = response.attributes['roomId'].value

            @client.observe room_id, "swordfish" do |success,response|
              logger.info "ObservationRequest: #{success}"

              if success
                room_handler = create_room_handler(round)
                @client.register_room_handler(room_id, room_handler)
                setup_clients(round, codes)
                setup_timeout(room_id, room_handler)
              else
                manager.close
                message = "Couldn't observe the game."
                logger.fatal message
                raise message
              end
            end
          else
            @client.close
            message = "Couldn't prepare game!"
            logger.fatal message
            raise message
          end
        rescue => e
          logger.log_formatted_exception e
          raise
        end
      end
    end

    protected

    def create_room_handler(round)
      logfile_name = "#{Time.now.to_i}_log_#{round.id}.xml.gz"
      logfile = Tempfile.new(logfile_name)
      gzip_logfile = Zlib::GzipWriter.open(logfile.path)
      ObservingRoomHandler.new gzip_logfile do |observer|
        begin
          logger.info "Logging done!"
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
          logger.info "Logfile: " + logfile_handle.original_filename
          logger.info ""
        ensure
          begin
            gzip_logfile.flush
            gzip_logfile.close
          rescue
            logger.warning "GZip writer could not be flushed"
          end
          logfile.close unless logfile.closed?
          logfile.unlink
          self.close
        end
      end
    end

    def setup_timeout(room_id, room_handler)
      # force gamestart
      Thread.new do
        begin
          logger.info "start_game_after #{SoChaManager.start_game_after} seconds"
          sleep SoChaManager.start_game_after

          threshold = [[SoChaManager.start_game_after, 20].min, 0].max

          if room_handler.done?
            logger.info "Game is already over, start_game_after not necessary."
          elsif !room_handler.received_data_after?(threshold.seconds.ago)
            logger.info "#{SoChaManager.start_game_after} seconds passed. Forcing game to start."
            #@client.step(room_id, true)
            logger.info "WARNING!: #{SoChaManager.start_game_after} seconds passed, game should be forces to start but isn't!"
          else
            logger.info "Action detected, start_game_after not necessary."
          end
        rescue => e
          logger.fatal "Timeout thread crashed!"
          logger.log_formatted_exception e
        end
      end
    end

    def setup_clients(round, codes)
      zip_files = []
      ActiveRecord::Base.benchmark "Preparing the Client VMs" do
        zip_files = round.slots.zip(codes).collect do |slot, code|
          start_client(slot, code)
        end.compact
      end

      logger.info "All clients have been prepared"

      if SoChaManager.emulate_vm
        logger.info "VM Emulation is turned on. Executing clients..."
        emulate_vm_watcher! zip_files
      end
    end

    def generate_startup_command(ai_program, reservation, silent)
      executable = ai_program.main_file_entry.file_name

      if executable.ends_with? ".jar"
        encoding = "-Dfile.encoding=UTF-8"
        memory_limit = "-Xmx1024M"
        jar = "-jar \"#{executable}\""
        executable = "java #{encoding} #{memory_limit} #{jar}"
      elsif executable.ends_with? ".exe"
        executable = "/usr/bin/wine \"#{executable}\""
      else
        executable = File.join(".", "\"#{executable}\"")
      end

      command = "#{executable} --host #{SoChaManager.server_host} --port #{SoChaManager.server_port} --reservation #{reservation}"
      command << " > /dev/null 2>&1" if silent

      logger.info "Startup.sh Command: #{command}"

      command
    end

    def start_client(slot, reservation)
      if slot.client
        logger.info "Preparing client (id=#{slot.client.id}, contestant='#{slot.name}')..."

        ai_program = slot.client
        zipped_ai_program = ai_program.file.path
        target = nil

        Dir.mktmpdir(File.basename(zipped_ai_program)) do |dir|
          unzip_to_directory(zipped_ai_program, dir)
          create_startup_script(ai_program, reservation, dir)
          target = zip_to_watch_folder(dir, slot)
        end

        target
      else
        logger.warn "Client for slot #{slot.id} is not set"
        nil
      end
    end

    def zip_to_watch_folder(source_directory, slot)
      key = slot.ingame_name
      #generated_file_name = "#{Time.now.to_i}_#{key}_#{(rand * 1000).ceil}.zip"
      # Be careful when altering this filename as it is used to identify the client id for the logs
      generated_file_name = "#{Time.now.to_i}_#{(rand * 1000).ceil}_#{key}_#{slot.client.id}.zip"
      target = File.expand_path(File.join(SoChaManager.watch_folder, generated_file_name))

      log_and_run %{sh -c "cd #{source_directory}; zip -qr #{target} ."}

      target
    end

    def create_startup_script(ai_program, reservation, dir)
      startup_file = File.join(dir, 'startup.sh')
      File.open(startup_file, 'w+') do |f|
        f.puts "#!/bin/bash"
        f.puts "echo \"Starting client\""
        f.puts "chmod +x #{ai_program.main_file_entry.file_name}"
        f.puts "if [ `head -c 2 #{ai_program.main_file_entry.file_name}` == '#!' ]"
        f.puts "then"
        f.puts "  tr -d [] < #{ai_program.main_file_entry.file_name} > #{ai_program.main_file_entry.file_name}_tmp"
        f.puts "  rm #{ai_program.main_file_entry.file_name}"
        f.puts "  mv #{ai_program.main_file_entry.file_name}_tmp #{ai_program.main_file_entry.file_name}"
        f.puts "fi"
        f.puts generate_startup_command(ai_program, reservation, SoChaManager.silent)
        f.puts "echo \"Client terminated\""
        f.flush
      end
      File.chmod(0766, startup_file)
    end

    def unzip_to_directory(file, dir)
      log_and_run %{unzip -qq #{File.expand_path(file)} -d #{File.expand_path(dir)}}
    end
  end
end
