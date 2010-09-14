class PersonAddedToContestantEvent < Event
  validates_presence_of :param_int_1
  validates_presence_of :param_int_2
  validates_presence_of :param_int_3

  def valid_event?
    if person.nil? or contestant.nil? or actor.nil?
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
      Person.find(self.param_int_1)
    rescue
      nil
    end
  end

  def actor=(c)
    self.param_int_3 = c.id
  end

  def actor
    begin
      Person.find(self.param_int_3)
    rescue
      nil
    end
  end

  def contestant=(c)
    self.param_int_2 = c.id
  end

  def contestant
    begin
      Contestant.find(self.param_int_2)
    rescue
      nil
    end
  end

  def path
    [:contest, contestant]
  end

  def text
    "#{I18n.t("events.person_added_to_contestant", :person => person.name, :contestant => contestant.name, :actor => actor.name)}"
  end

end
