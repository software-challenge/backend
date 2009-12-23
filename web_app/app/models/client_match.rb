class ClientMatch < Match
  validates_uniqueness_of :set_id, :scope => :set_type
  def clients=(clients)
    Match.transaction do
      clients.each do |client|
        slots.create!(:client => client)
      end
      create_rounds!
    end
  end
end