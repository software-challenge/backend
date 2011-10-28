class TimeEntryAddedEvent < Event
  belongs_to :person, :foreign_key => "param_int_2"
  belongs_to :time_entry, :foreign_key => "param_int_2"
  validates_presence_of :time_entry

  def valid_event?
    valid? 
  end

  def path
    [time_entry]
  end

  def text
    I18n.t("events.report_edited", :contestant => time_entry.context.name, :editor => person.name)
  end

end
