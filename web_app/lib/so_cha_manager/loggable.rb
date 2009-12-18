module SoChaManager
  module Loggable
    if defined? ActiveRecord::Base and ActiveRecord::Base.logger
      @@logger = ActiveRecord::Base.logger
    else
      @@logger = Logger.new(STDOUT)
    end
    
    def self.included(base)
      base.instance_eval do
        define_method :logger do
          @@logger
        end
      end
    end
  end
end