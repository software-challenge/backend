class DailyJob < ScheduledJobData

  def priority
    Match::DAILY_PRIORITY
  end

  def time
    {:hour => 3, :minute => 0, :second => 0}
  end

  def perform
    # Perform actions
    logger.info "Performing daily actions"     
    autoplay_days
    send_notifications
    remove_unvalidated_people
    logger.info "Daily action done"
  end

  def remove_unvalidated_people
    Person.all.each do |person|
      unless person.validated?
        if person.created_at + 48.hours < DateTime.now      
          person.destroy
        end
      end 
    end
  end
 
  def send_notifications
    logger.info "Send automatic E-Mail notifications"
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
    logger.info "Autoplay matchdays"
    offset = 1
    Contest.all.each do |contest|
      if contest.ready? and contest.play_automatically?
        matchdays = contest.matchdays(:reload).find_all{|md| (md.when.past? or md.when.today?) and not md.played?}.sort_by(&:position)
        matchdays.each do |md|
          Matchday.transaction do
            md.load_active_clients!
            Delayed::Job.enqueue Delayed::PerformableMethod.new(md, :perform_delayed!, []), Match::MATCHDAY_PRIORITY, offset.minutes.from_now
          end
          offset += 1
        end
      end
    end 
  end

end
