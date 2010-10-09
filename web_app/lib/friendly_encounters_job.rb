# This job looks for ready but still unplayed friendly encounters and starts them if allowed (next day i.e.)

class FriendlyEncountersJob < ScheduledJobData

  def priority
    Match::FRIENDLY_PRIORITY
  end

  def time
    {:hour => 0, :minute => 0, :second => 0}
  end

  def perform
    Delayed::Worker.logger.info "Start playing friendly encounters"
    FriendlyEncounter.all.each do |enc|
      if enc.ready? and enc.playable?
        enc.play!
      end
    end
    Delayed::Worker.logger.info "All friendly encounters played"
  end

end
