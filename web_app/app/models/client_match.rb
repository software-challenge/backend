class ClientMatch < Match
  validates_uniqueness_of :set_id, :scope => :set_type, :if => :validate_uniqueness_of_set_id?
  def validate_uniqueness_of_set_id?
    true
  end


  #belongs_to :client
  delegate :contestant, :to => :client

  def client
    slots.first.client
  end

  def clients=(clients)
    Match.transaction do
      self.save!
      Match.transaction do
        clients.each do |client|
          slots.create!(:client => client)
        end
        create_rounds!(game_definition.test_rounds)
      end
    end
  end

  def perform_delayed!(activateClient = false)
    job_id = Delayed::Job.enqueue(TestJob.new(self.id, activateClient), priority)
    self.job = Delayed::Job.find(job_id)
    save!
  end

  def perform(activateClient = false)
    super()
    logger.debug "ClientMatch performed"
    if activateClient and client.ok?
      c = client.contestant
      logger.debug "Activating client: #{client}"
      c.current_client = client
      c.save! 
    end
  end

  def priority
    Match::LOW_PRIORITY
  end
end

