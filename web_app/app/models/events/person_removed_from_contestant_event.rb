class PersonRemovedFromContestantEvent < PersonAddedToContestantEvent

  def path
    [:contest, person]
  end

  def text
    "#{I18n.t("events.person_removed_from_contestant", :person => person.name, :contestant => contestant.name, :actor => actor.name)}"
  end

end
