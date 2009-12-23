class LeagueMatchSlot < MatchSlot
  validates_presence_of :matchday_slot
  belongs_to :matchday_slot

  delegate :contestant, :to => :matchday_slot
end