SO_CHA_MANAGER_LOGGER = Logger.new(Rails.root.join('log', 'sc_manager.log'))
SO_CHA_MANAGER_LOGGER.level = Logger::INFO

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