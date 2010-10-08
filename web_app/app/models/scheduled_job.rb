class ScheduledJob < ActiveRecord::Base

  validates_uniqueness_of :name

  def check(job)
    if (not running) or (last_check + (job.check_interval + 1).seconds < DateTime.now)
      thread = Thread.new {
        jentry = ScheduledJob.find_by_name(job.class.to_s)
        begin
          while(true) do
            sleep job.check_interval
            unless job_already_started?(job)
              Delayed::Job.enqueue job, job.priority, DateTime.now.to_time.in_time_zone("UTC").tomorrow.change(job.time)
            end
            jentry.last_check = DateTime.now
            jentry.save!
          end
        ensure
          jentry.running = false
          jentry.save!
        end
      }
      thread.run
      puts "Scheduled job check added: #{name}"
      self.running = true
      self.save!
    end
    self.last_check = DateTime.now
    self.save!
  end

  def job_already_started?(job)
    not Delayed::Job.all(:reload).to_a.find{|j| j.name == job.class.to_s}.nil?
  end

end
