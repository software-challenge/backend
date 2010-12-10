require 'zip/zip'

class FakeTest < ActiveRecord::Base

  has_and_belongs_to_many :clients  
  belongs_to :job, :dependent => :destroy, :class_name => "Delayed::Job"
  has_many :checks, :class_name => "FakeCheck"

  validate do |record|
    if record.clients.length < 2
      record.errors.add :clients, "must at least contain 2 clients"
    end
  end

  HIGH_PRIORITY = 10 # finals
  MEDIUM_PRIORITY = 5
  MATCHDAY_PRIORITY = 4
  FRIENDLY_PRIORITY = 2
  DAILY_PRIORITY = 3
  LOW_PRIORITY = 0 # client-tests
 

def perform
  puts "FakeTest :"+id.to_s+" wird ausgeführt"
  checks.each do |check|
   unless check.done?
    check.perform
   end
  end
  save!
  puts "FakeTest :"+id.to_s+" wurde erfolgreich beendet"
end

def perform_delayed!
   job_id = Delayed::Job.enqueue self, priority
   self.job = Delayed::Job.find(job_id)
   save!
end

def done?
  checks.inject(true){|s,e| s&=e.done?}
end

def priority
  LOW_PRIORITY
end

def reset!
  checks.each{|check| check.delete}
end

def reset_results!
  checks.each{|check| check.reset!}
end 

def restart!
  reset_results!
  perform_delayed!
end

def contest
  client.first.contest
end

end

