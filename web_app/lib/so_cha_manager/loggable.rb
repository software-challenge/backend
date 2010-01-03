SO_CHA_MANAGER_LOGGER = Logger.new(Rails.root.join('log', 'sc_manager.log'))
SO_CHA_MANAGER_LOGGER.level = Logger::INFO
SO_CHA_MANAGER_LOGGER.formatter = Logger::Formatter.new
SO_CHA_MANAGER_LOGGER.datetime_format = "%Y-%m-%d"
SO_CHA_MANAGER_LOGGER.info "SoChaManager Logger instanciated at #{Time.now}"

module SoChaManager
  module Loggable
    def self.included(base)
      base.extend SoChaManager::Loggable
    end

    def logger
      SO_CHA_MANAGER_LOGGER
    end
  end
end