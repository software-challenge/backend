#!/usr/bin/env ruby

ENV["RAILS_ENV"] ||= "production"

require File.dirname(__FILE__) + "/../../config/environment"

$sjobs_running = true

Signal.trap("TERM") do
  $sjobs_running = false
end

Thread.new do
  while($sjobs_running) do
    sleep 1
  end
end

logger = Logger.new(Rails.root.join("log/scheduled_jobs_daemon.log"))
logger.info "Scheduled jobs daemon started at #{Time.now}."

timer = 0
while($sjobs_running) do
  if timer >= 300
    logger.info "The scheduled jobs daemon is still running at #{Time.now}."
  end
  log_directory = File.expand_path(Rails.root.join("log"))

  DailyJob.new.schedule
  FriendlyEncountersJob.new.schedule  

  sleep 15
  timer += 15
end 
