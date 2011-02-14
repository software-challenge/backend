module SoChaManager
  module Executor
    def play(round)
      manager = self
      player_names = round.slots.collect(&:ingame_name)
      @client.log round if true or round.match.type == "LeagueMatch"

      @client.prepare round.game_definition.plugin_guid, round.slots.collect(&:name) do |success,response|
        begin
          if success
            logger.info "Game has been prepared"

            reservations = response.xpath '//reservation'
            codes = reservations.collect(&:content)
            codes.each do |reservation|
              logger.info "Reservation ID: #{reservation}"
            end
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


    def create_room_handler(round)
      logfile_name = "#{Time.now.to_i}_log_#{round.id}.xml.gz"
      logfile = Tempfile.new(logfile_name)
      ObservingRoomHandler.new logfile do |observer|
        begin
          logger.info "Logging done!"
          observer.data.open # reopen for reading
          tmp_zip_file = Tempfile.new(logfile_name)
          zip_file = Zlib::GzipWriter.new(tmp_zip_file)
          zip_file.write(observer.data.read)
          zip_file.close
          observer.data.close

          # add original_filename attribute for Paperclip
          logfile_handle = tmp_zip_file.open
          def logfile_handle.original_filename=(x); @original_filename = x; end
          def logfile_handle.original_filename; @original_filename; end
          logfile_handle.original_filename = logfile_name

          # save replay to database
          round.replay = logfile_handle
          round.save!
          # file is closed by paperclip
          logger.info "Logfile: " + logfile_handle.original_filename

          @last_result = observer.result
          
        ensure
          logger.info "Cleaning up"
          tmp_zip_file.close unless tmp_zip_file.closed?
          tmp_zip_file.unlink
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
          logger.info "Game will be forced to start in #{SoChaManager.start_game_after} seconds"
          sleep SoChaManager.start_game_after

          threshold = [[SoChaManager.start_game_after, 20].min, 0].max

          if room_handler.done?
            logger.info "Game is already over, start_game_after not necessary."
          elsif !room_handler.received_data_after?(threshold.seconds.ago)
            logger.info "#{SoChaManager.start_game_after} seconds passed. Forcing game to start."
            @client.step(room_id, true)
            #logger.info "WARNING!: #{SoChaManager.start_game_after} seconds passed, game should be forces to start but isn't!"
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

      if !SoChaManager.emulate_vm
        logger.info "Waiting for clients to be moved to a VM"
        all_moved = false
        while !all_moved
          all_moved = true
          @client.reset_timeout
          zip_files.each do |zipfile|
            if File.exists? zipfile or File.exists?(File.join(SoChaManager.client_tmp_folder, File.basename(zipfile)))
              all_moved = false
            end
          end
          sleep 1
        end
        logger.info "Room timeout in #{SoChaManager.timeout} seconds"
      end
    end

    def generate_startup_command(ai_program, reservation, silent)
      return "echo \"ERROR: No main file set!\"" if ai_program.main_file_entry.nil?
      executable = ai_program.main_file_entry.file_name

      if executable.ends_with? ".jar"
        encoding = "-Dfile.encoding=UTF-8"
        memory_limit = "-Xmx1250M"
        jar = "-jar \"#{executable}\""
        executable = "java #{encoding} #{memory_limit} #{jar}"
      elsif executable.ends_with? ".exe"
        executable = "/usr/bin/wine \"#{executable}\""
      else
        executable = File.join(".", "\"#{executable}\"")
      end

      command = "#{executable} --host #{SoChaManager.server_host} --port #{SoChaManager.server_port} --reservation #{reservation}"
      unless ai_program.parameters.blank?
        command << " #{ai_program.parameters}"
      end
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
          create_startup_script(slot, reservation, dir)
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

    def create_startup_script(slot, reservation, dir)
      ai_program = slot.client
      startup_file = File.join(dir, 'startup.sh')
      startup_command = generate_startup_command(ai_program, reservation, SoChaManager.silent)
      logfile_dir = case slot.round.match.type.to_s
                    when "ClientMatch"
                      "test"
                    when "LeagueMatch", "FinaleMatch"
                      File.join("match", slot.round.match.id.to_s, slot.round.id.to_s)
                    when "CustomMatch"
                      File.join("custom", slot.round.match.id.to_s, slot.round.id.to_s)
                    when "FriendlyMatch"
                      File.join("friendly", slot.round.match.id.to_s, slot.round.id.to_s)
                    else
                      "unknown"
                    end
      logger.info "Logfile directory: #{logfile_dir}" 
      File.open(startup_file, 'w+') do |f|
        f.puts "#!/bin/bash"
        f.puts "echo \"Logfile directory: #{logfile_dir}\""
        unless ai_program.main_file_entry.nil?
          f.puts "echo \"Startup command: #{startup_command}\""
          f.puts "echo \"Starting client\""
          f.puts "if [ `head -c 2 \"#{ai_program.main_file_entry.file_name}\"` == '#!' ]"
          f.puts "then"
          f.puts "  tr -d  < \"#{ai_program.main_file_entry.file_name}\" > \"#{ai_program.main_file_entry.file_name}_tmp\""
          f.puts "  rm \"#{ai_program.main_file_entry.file_name}\""
          f.puts "  mv \"#{ai_program.main_file_entry.file_name}_tmp\" \"#{ai_program.main_file_entry.file_name}\""
          f.puts "fi"
          f.puts "chmod +x \"#{ai_program.main_file_entry.file_name}\""
        end
        f.puts "touch $HOME/started"
        f.puts startup_command
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
