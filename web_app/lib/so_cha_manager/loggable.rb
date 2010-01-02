module SoChaManager
  module Loggable
    @@logger = Logger.new(Rails.root.join('log', 'sc_manager.log'))
    @@logger.level = Logger::WARN

    def self.included(base)
      base.extend SoChaManager::Loggable
    end

    def logger
      @@logger
    end
  end
end