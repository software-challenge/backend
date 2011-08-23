class NewSchoolEvent < Event
  has_one :season, :through => :school
  belongs_to :school, :foreign_key => :param_int_1
  validates_presence_of :param_int_1

  def valid_event?
    if school.nil?
      self.destroy
      false
    else
      true
    end
  end

  def path
    [season, school]
  end

  def text
    "Die Schule \"#{school.name}\"  wurde angemeldet"
  end
end
