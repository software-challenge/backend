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
        create_rounds!
      end
    end
  end
end