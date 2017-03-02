#!/bin/bash

export RAILS_ENV=production
echo $(date): starting check for running jobs...
cd /home/scadmin/rails-deployment/current
raw_output=$(bundle exec ./script/runner 'puts !Delayed::Job.all.select{|j| j.locked_at != nil && j.last_error.nil?}.empty?' 2>/dev/null)
split=( $raw_output )
running_jobs=${split[@]: -1}
if [[ $running_jobs == "true" ]]
then
  echo running jobs present
elif [[ $running_jobs == "false" ]]
then
  echo no running jobs, restarting delayed jobs...
  supervisorctl restart delayed_job
else
  echo unexpected value: $running_jobs, raw output was: $raw_output >&2
  echo unexpected value: $running_jobs, raw output was: $raw_output >&1
fi
