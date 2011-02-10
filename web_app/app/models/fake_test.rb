require 'zip/zip'

class FakeTest < ActiveRecord::Base

  has_one :contest, :through => :fake_test_suite
  belongs_to :fake_test_suite
  has_and_belongs_to_many :clients  
  belongs_to :job, :dependent => :destroy, :class_name => "Delayed::Job"
  has_many :checks, :class_name => "FakeCheck", :dependent => :destroy
  delegate :name, :to => :fake_test_suite
  delegate :description, :to => :fake_test_suite

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
 
def started?
  started_at ? started_at.past? : false
end

def perform
  puts "FakeTest: #{id} wird ausgeführt"
  started_at = Time.now
  save!
  checks.each do |check|
   unless check.done?
    check.perform
    check.finished_at = Time.now
    check.save!
   end
  end
  save!
  puts "FakeTest: #{id} wurde erfolgreich beendet"
  fake_test_suite.handle_event!
end

def perform_delayed!
   job_id = Delayed::Job.enqueue self, priority
   self.job = Delayed::Job.find(job_id)
   save!
end

def done?
  checks.inject(true){|s,e| s&=e.done?} and job.nil?
end

def running?
  not done? and not job.nil? and !!job.locked_at
end

def priority
  LOW_PRIORITY
end

def state
  if done?
    return 'finished'
  elsif running?
    return 'idle'
  elsif started?
    return 'error'
  else 
    return 'ready'
  end
end


def self.available_checks
  checks_folder = File.join(RAILS_ROOT, "app", "models", "fake_checks")
  Dir.open(checks_folder).select{|f| not f.starts_with(".") and f.ends_with("_check.rb") and not f == "fake_check.rb"}.collect{|c| eval c.delete(".rb").camelcase}
end

def self.available_checks_for_contest(contest)
  available_checks.select{|c| c.compatible_with_contest? contest}
end

def reset_results!
  FakeTest.transaction do
    self.started_at = nil
    checks.each{|check| check.reset!}
    self.save!
  end
end 

def restart!
  reset_results!
  perform_delayed!
end

end

