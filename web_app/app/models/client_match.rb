class ClientMatch < Match
  validates_uniqueness_of :set_id, :scope => :set_type

  belongs_to :client
  delegate :contestant, :to => :client

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

  def priority
    Match::LOW_PRIORITY
  end
end