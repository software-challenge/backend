#!/usr/bin/env ruby

# You might want to change this
ENV["RAILS_ENV"] ||= "production"

require File.dirname(__FILE__) + "/../../config/environment"

$running = true
$game_server_pid = nil

Signal.trap("TERM") do 
  $running = false
  Process.kill("TERM", $game_server_pid)
end

# SIGNAL response thread
Thread.new do
  while($running) do
    sleep 1
  end
end

logger = Logger.new(Rails.root.join("log/game_server_daemon.log"))

while($running) do
  
  # Replace this with your code
  logger.info "This daemon is still running at #{Time.now}.\n"
  log_directory = File.expand_path(Rails.root.join("log"))
  
  Dir.chdir Rails.root.join("public", "server") do
    args = ['java',
      '-Dfile.encoding=UTF-8',
      '-Dlogback.configurationFile=logback-release.xml',
      "-DLOG_DIRECTORY=#{log_directory}",
      '-jar', './softwarechallenge-server.jar']
    
    logger.info "Starting Server: #{args.join(' ')}"

    $game_server_pid = Process.fork do
      exec(*args)
    end

    pid, process_status = Process.wait2($game_server_pid)

    logger.info "The GameServer exited with exitstatus=#{process_status.exitstatus}.\n"
  end

  # try restart after 5 seconds
  sleep 5
end
