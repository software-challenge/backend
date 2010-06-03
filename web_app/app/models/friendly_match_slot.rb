class FriendlyMatchSlot < MatchSlot
  validates_presence_of :friendly_encounter_slot
  belongs_to :friendly_encounter_slot, :foreign_key => "matchday_slot_id"

  undef :contestant
  undef :client=
  undef :client  
  
  delegate :contestant, :client, :client=, :to => :friendly_encounter_slot

end
