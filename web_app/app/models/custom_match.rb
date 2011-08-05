class CustomMatch < ClientMatch
  def validate_uniqueness_of_set_id?
    false
  end

  def contest
    self.set
  end

  def contest=(con)
    self.set = con
    self.set
  end

  def perform_delayed!(prio)
    job_id = Delayed::Job.enqueue(self, prio)
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
