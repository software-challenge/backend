class CustomMatch < ClientMatch
  LOW_PRIORITY = 0

  def validate_uniqueness_of_set_id?
    false
  end

  undef contest

  def contest
    self.set
  end

  def contest=(con)
    self.set = con
    self.set
  end

  def perform_delayed!
    job_id = Delayed::Job.enqueue(self, priority)
    self.job = Delayed::Job.find(job_id)
    save!
  end

  def perform
    super()
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
end
