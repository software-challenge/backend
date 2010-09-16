class DailyJob

  def add_daily_job_check
    thread = Thread.new {
      while(true) do
        unless job_already_started?
          Delayed::Job.enqueue self, Match::DAILY_PRIORITY, DateTime.now.to_time.in_time_zone("UTC").tomorrow.change(:hour => 3, :minute => 0, :second => 0)
        end
        sleep 15
      end
    }
    thread.run
  end

  def job_already_started?
    not Delayed::Job.all(:reload).to_a.find{|job| job.name == self.class.to_s}.nil?
  end

  def perform
    autoplay_days
    send_notifications
  end
 
  def send_notifications
    Contest.all.each do |contest|
      md = contest.upcoming_matchday
      unless md.nil?
        if md.when == DateTime.tomorrow
          md.slots.each do |slot|
            cont = slot.contestant
            if cont.current_client.nil? or not cont.current_client.tested? or not cont.current_client.ok?
              EventMailer.deliver_client_matchday_warning_notification(cont, md)
            end
          end
        end
      end
    end  
  end

  def autoplay_days
    offset = 1
    Contest.all.each do |contest|
      if contest.ready? and contest.play_automatically?
        matchdays = contest.matchdays(:reload).find_all{|md| (md.when.past? or md.when.today?) and not md.played?}.sort_by(&:position)
        matchdays.each do |md|
          Delayed::Job.enqueue Delayed::PerformableMethod.new(md, :perform_delayed!, []), Match::MATCHDAY_PRIORITY, offset.minutes.from_now
          offset += 1
        end
      end
    end 
  end

end
