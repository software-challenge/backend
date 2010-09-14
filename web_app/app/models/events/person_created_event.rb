class PersonCreatedEvent < Event
  
  validates_presence_of :param_int_1
  validates_presence_of :param_int_2
  
  def valid_event?
    if person.nil? or creator.nil?
      self.destroy
      false
    else
      true
    end
  end

  def person=(p)
    self.param_int_1 = p.id
  end

  def person
    begin
      Person.find(param_int_1)
    rescue
      nil
    end  
  end

  def creator=(c)
    self.param_int_2 = c.id
  end

  def creator
    begin
      Person.find(param_int_2)
    rescue
      nil
    end
  end

  def text
    "#{I18n.t("events.person_created", :person => person.name, :creator => creator.name)}"
  end

  def path
    [:contest, person]
  end

end
