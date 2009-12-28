#!/usr/bin/env ruby

# You might want to change this
ENV["RAILS_ENV"] ||= "production"

require File.dirname(__FILE__) + "/../../config/environment"

$running = true
Signal.trap("TERM") do 
  $running = false
end

while($running) do
  
  # Replace this with your code
  ActiveRecord::Base.logger.info "This daemon is still running at #{Time.now}.\n"
  
  Dir.chdir Rails.root.join("public", "server") do
    `java -jar GameServer.jar`
    process_status = $?
    ActiveRecord::Base.logger.info "The GameServer exited with exitstatus=#{process_status.exitstatus}.\n"
  end

  # try restart after 5 seconds
  sleep 5
end