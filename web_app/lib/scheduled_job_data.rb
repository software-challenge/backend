class ScheduledJobData

  def check_interval
    15
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
    if ScheduledJob.table_exists?
      jentry = ScheduledJob.find_by_name(self.class.to_s)
      if jentry.nil?
        jentry = ScheduledJob.create :name => self.class.to_s, :running => false
      end
      jentry.save!
      jentry.check(self)
    end
  end
end
