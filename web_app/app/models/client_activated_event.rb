class ClientActivatedEvent < Event
  validates_presence_of :param_int_1
  
  def client=(client)
    self.param_int_1 = client.id
  end

  def client
    Client.find(param_int_1)
  end

  def contestant
    client.contestant
  end
end
