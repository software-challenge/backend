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
        ensure
          self.close
          logfile.close unless logfile.closed?
          logfile.unlink
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
            logger.info "Invoking handler for start_game_after."
            @client.step(room_id, true)
          else
            logger.info "Action detected, start_game_after not necessary."
          end
        rescue => e
          logger.log_formatted_exception e
        end
      end
    end

    def setup_clients(round, codes)
      zip_files = []
      ActiveRecord::Base.benchmark "Preparing the Client VMs" do
        zip_files = round.slots.zip(codes).collect do |slot, code|
          start_client(slot, code)
        end
      end

      logger.info "All clients have been prepared"

      if SoChaManager.emulate_vm
        logger.info "VM Emulation is turned on. Executing clients..."
        emulate_vm_watcher! zip_files
      end
    end

    def generate_startup_command(ai_program, reservation, silent)
      executable = ai_program.main_file_entry.file_name

      if ai_program.java?
        executable = "java -Dfile.encoding=UTF-8 -jar #{executable}"
      else
        executable = File.join(".", executable)
      end

      command = "#{executable} --host #{SoChaManager.server_host} --port #{SoChaManager.server_port} --reservation #{reservation}"
      command << " > /dev/null 2>&1" if silent

      logger.info "startup.sh << #{command}"

      command
    end

    def start_client(slot, reservation)
      logger.info "Starting client (id=#{slot.client.id}) for '#{slot.name}'"

      ai_program = slot.client
      zipped_ai_program = ai_program.file.path
      target = nil

      Dir.mktmpdir(File.basename(zipped_ai_program)) do |dir|
        unzip_to_directory(zipped_ai_program, dir)
        create_startup_script(ai_program, reservation, dir)
        target = zip_to_watch_folder(dir, slot)
      end

      target
    end

    def zip_to_watch_folder(source_directory, slot)
      key = slot.ingame_name
      generated_file_name = "#{Time.now.to_i}_#{key}_#{(rand * 1000).ceil}.zip"
      target = File.expand_path(File.join(SoChaManager.watch_folder, generated_file_name))

      command = %{sh -c "cd #{source_directory}; zip -qr #{target} ."}
      logger.info command
      system command

      target
    end

    def create_startup_script(ai_program, reservation, dir)
      startup_file = File.join(dir, 'startup.sh')
      File.open(startup_file, 'w+') do |f|
        f.puts "#!/bin/bash"
        f.puts generate_startup_command(ai_program, reservation, SoChaManager.silent)
        f.flush
      end
      File.chmod(0766, startup_file)
    end

    def unzip_to_directory(file, dir)
      command = %{unzip -qq #{File.expand_path(file)} -d #{File.expand_path(dir)}}
      logger.info command
      system command
    end
  end
end