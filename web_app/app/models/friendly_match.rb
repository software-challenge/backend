class FriendlyMatch < Match
  LOW_PRIORITY = 0

  has_many :slots, :class_name => "FriendlyMatchSlot", :dependent => :destroy, :order => "position", :foreign_key => "match_id"

  alias :friendly_encounter :set
  alias :matchday :set
  alias :friendly_encounter= :set=

  def validate_uniqueness_of_set_id?
    false
  end

  def contestants=(contestants)
    Match.transaction do
      contestants.each do |contestant|
        if contestant
          slots.create!(:friendly_encounter_slot => friendly_encounter.slots.first(:conditions => {:contestant_id => contestant.id}))
        else
          slots.create!
        end
      end
      create_rounds!
    end
  end

  def perform_delayed!
    job_id = Delayed::Job.enqueue(self, priority)
    self.job = Delayed::Job.find(job_id)
    save!
  end

  def perform
    super()
    logger.debug "FriendlyMatch performed" 
  end

  def setup_clients(clients, rounds)
    Match.transaction do
      self.save!
      Match.transaction do
        clients.each do |client|
          slots.create!(:client => client)
        end
        create_rounds!(rounds)
      end
    end
  end

  def load_active_clients(force_reload = false)
    slots.each do |friendly_match_slot|
      slot = friendly_match_slot.friendly_encounter_slot
      if (slot.client.nil? or force_reload)
        slot.client = slot.contestant.current_client
        slot.save!
      end    
    end
  end
end
