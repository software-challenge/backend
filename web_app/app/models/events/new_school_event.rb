class NewSchoolEvent < Event
  validates_presence_of :param_int_1

  def valid_event?
    if school.nil?
      self.destroy
      false
    else
      true
    end
  end

  def school=(school)
    self.param_int_1 = school.id
  end

  def school
    begin
      School.find(param_int_1)
    rescue
      nil
    end 
  end

  def path
    [:contest, school]
  end

  def text
    "Die Schule \"#{school.name}\"  wurde angemeldet"
  end
end
