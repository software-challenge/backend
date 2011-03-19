$:.unshift File.join(File.dirname(__FILE__), 'manager')
require 'configuration'
require 'executor'
require 'packer'
require 'vm_emulator'

module SoChaManager

  require 'tempfile'
  require 'yaml'

  include Loggable
  extend SoChaManager::Configuration
    
  class Manager
    
    include Loggable
    
    include SoChaManager::Packer
    include SoChaManager::VmEmulator
    include SoChaManager::Executor

    unless File.exists?(SoChaManager.watch_folder) and File.directory?(SoChaManager.watch_folder)
      logger.fatal "watch_folder (#{SoChaManager.watch_folder}) isn't a directory or does not exist."
    end

    attr_reader :last_result, :client, :threads
    delegate :done?, :to => :client
    
    def close
      logger.info "Closing client"
      @client.close
      logger.info "Closing threads"
      @threads.each do |t|
        t.kill
        logger.info "Thread #{t} closed"
      end
    end

    def connect!
      @threads = []
      @client = Client.new SoChaManager.server_host, SoChaManager.server_port
    end
    
    def log_and_run(command)
      logger.debug command
      system command
    end

  end
end
