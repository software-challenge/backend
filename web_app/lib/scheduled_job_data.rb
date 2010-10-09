class ScheduledJobData

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
      Delayed::Job.enqueue self, priority, DateTime.now.to_time.in_time_zone("UTC").tomorrow.change(time)
    end
  end

  def job_already_started?
    not Delayed::Job.all(:reload).to_a.find{|job| job.name == self.class.to_s}.nil?
  end

end
