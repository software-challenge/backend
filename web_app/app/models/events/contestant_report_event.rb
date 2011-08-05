class ContestantReportEvent < Event
  belongs_to :contestant, :foreign_key => "param_int_1"
  belongs_to :person, :foreign_key => "param_int_2"
  belongs_to :contest
  validates_presence_of :contest
  validates_presence_of :contestant
  validates_presence_of :person

  def valid_event?
    valid? 
  end

  def path
    ["report", contest, contestant]
  end

  def text
    I18n.t("events.report_edited", :contestant => contestant.name, :editor => person.name)
  end

end
