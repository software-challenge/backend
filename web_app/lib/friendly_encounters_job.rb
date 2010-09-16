# This job looks for ready but still unplayed friendly encounters and starts them if allowed (next day i.e.)

class FriendlyEncountersJob

  def add_friendly_encounter_check
    thread = Thread.new {
      while(true) do
        # First check if there still is such a job running
        unless job_already_started?
          Delayed::Job.enqueue self, Match::FRIENDLY_PRIORITY, DateTime.now.to_time.in_time_zone("UTC").tomorrow.change(:hour => 0, :minute => 0, :second => 0)
        end
        sleep 15
      end
    }
    thread.run
  end

  def job_already_started?
    not Delayed::Job.all.find{|job| job.name == self.class.to_s}.nil?
  end

  def perform
    FriendlyEncounter.all.each do |enc|
      if enc.ready? and enc.playable?
        enc.play!
      end
    end
  end

end
