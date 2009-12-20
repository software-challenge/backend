module SoChaManager
  module Loggable    
    def self.included(base)
      base.instance_eval do
        define_method :logger do
          ActiveRecord::Base.logger
        end
      end
    end
  end
end