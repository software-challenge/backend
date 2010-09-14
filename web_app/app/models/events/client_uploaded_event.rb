class ClientUploadedEvent < Event
  validates_presence_of :param_int_1

  def valid_event?
    if client.nil? or contestant.nil?
      self.destroy
      false
    else
      true
    end
  end

  def client=(client)
    self.param_int_1 = client.id
  end

  def client
    begin
      Client.find(param_int_1)
    rescue
      nil
    end
  end

  def contestant
    client.contestant
  end

  def path
    [:contest, contestant]
  end

  def text
    "#{contestant.name} #{I18n.t("events.client_uploaded")}"
  end

end
