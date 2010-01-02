module SoChaManager
  module VmEmulator
    def emulate_vm_watcher!(zip_files)
      logger.info "Starting clients without VM"
      zip_files.each { |path| logger.info path }

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

    def run_without_vm!(path)
      # make it absolute
      path = File.expand_path(path)

      # assert that we have the output directory
      output_directory = File.join(RAILS_ROOT, 'tmp', 'vmwatch_extract')
      Dir.mkdir output_directory unless File.directory? output_directory

      # create a directory to extrat the zip file
      directory = File.join(output_directory, File.basename(path))
      Dir.mkdir directory

      validate_zip_file(path)

      # extract
      logger.info "Extracting AI program..."
      full_output_path = File.expand_path(directory)
      log_and_run %{unzip -oqq #{path} -d #{full_output_path}}

      raise "failed to unzip" unless $?.exitstatus == 0

      logger.info "Starting AI program and waiting for termination..."
      log_and_run %{sh -c "cd #{full_output_path}; ./startup.sh"}

      logger.info "AI program has been executed and returned (exitcode: #{$?.exitstatus})"
    end

    def validate_zip_file(path)
      # check zip for defects
      logger.info "Checking zip-file for defects..."
      log_and_run %{unzip -qqt #{path}}

      # repair if broken
      unless $?.exitstatus == 0
        logger.info "Zip-file is broken. Trying to fix..."

        fixed_path = "#{path}.fixed"
        log_and_run %{zip -qFF #{path} --out #{fixed_path}}

        raise "Couldn't fix broken zip-file" unless $?.exitstatus == 0

        logger.info "Sucessfully fixed zip-file"
        File.unlink(path)
        File.move(fixed_path, path)
      end
    end
  end
end