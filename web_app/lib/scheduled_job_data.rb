class ScheduledJobData
  
  def logger
    unless $sjobs_logger
      $sjobs_logger = Logger.new(Rails.root.join("log/scheduled_jobs_daemon.log"))
    end
    $sjobs_logger
  end

  def priority
    0
  end

  def time
    {:hour => 0, :minute => 0, :second => 0}
  end 

  def perform
  end

  def schedule
    unless job_already_started?
      logger.info "Scheduling job: #{self.class.to_s}"
      Delayed::Job.enqueue self, priority, DateTime.now.to_time.in_time_zone("UTC").tomorrow.change(time)
    end
  end

  def job_already_started?
    not Delayed::Job.all(:reload).to_a.find{|job| job.name == self.class.to_s}.nil?
  end

end
