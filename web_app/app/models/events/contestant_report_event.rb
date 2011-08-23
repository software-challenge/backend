class ContestantReportEvent < Event
  belongs_to :contestant, :foreign_key => "param_int_1"
  belongs_to :person, :foreign_key => "param_int_2"
  validates_presence_of :contestant
  validates_presence_of :person
  validates_presence_of :context

  def valid_event?
    valid? 
  end

  def path
    ["report", context, contestant]
  end

  def text
    I18n.t("events.report_edited", :contestant => contestant.name, :editor => person.name)
  end

end
