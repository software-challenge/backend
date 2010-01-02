module SoChaManager
  module Configuration
    def self.extended(base)
      configuration = YAML.load_file(Rails.root.join('config', 'vm_watch.yml'))[Rails.env]

      base.instance_eval do
        class_variable_set :@@configuration, configuration
        class_variable_set :@@watch_folder, (File.expand_path(configuration["watch_folder"], Rails.root) rescue Rails.root.join('tmp', 'vmwatch'))

        { :timeout => 60,
          :start_game_after => 30,
          :emulate_vm => false,
          :server_host => "localhost",
          :server_port => 13050,
          :silent => true
        }.each do |k,default|
          value = (configuration[k.to_s] || default)
          class_variable_set :"@@#{k}", value
        end

        mattr_reader :configuration
        mattr_reader :watch_folder, :emulate_vm
        mattr_reader :timeout, :start_game_after
        mattr_reader :server_host, :server_port
        mattr_reader :silent

        logger.info "Configuration: #{self.configuration.inspect}" if base.respond_to? :logger
      end
    end
  end
end