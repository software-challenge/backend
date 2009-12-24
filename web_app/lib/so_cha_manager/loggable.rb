module SoChaManager
  module Loggable    
    def self.included(base)
      base.instance_eval do
        @@logger = nil

        define_method :logger do
          # ActiveRecord::Base.logger
          @@logger ||= Logger.new(Rails.root.join('log', 'sc_manager.log'))
        end
      end
    end
  end
end