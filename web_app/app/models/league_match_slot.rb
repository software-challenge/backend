class LeagueMatchSlot < MatchSlot
  validates_presence_of :matchday_slot
  belongs_to :matchday_slot

  undef :contestant=
  undef :contestant
  undef :client=
  undef :client

  delegate :contestant, :client, :to => :matchday_slot
end