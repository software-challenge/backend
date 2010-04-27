class LeagueMatchSlot < MatchSlot
  validates_presence_of :matchday_slot
  belongs_to :matchday_slot

  undef :contestant
  undef :client=
  undef :client

  delegate :matchday, :contestant, :client, :client=, :to => :matchday_slot

  def has_same_score_as? slot
    return if self.score.nil? or slot.score.nil?
    matchday.contest.game_definition.match_score.values.each do |frag|
      if self.score.fragments.find(:first, :conditions => {:fragment => frag.name.to_s}).value != slot.score.fragments.find(:first, :conditions => {:fragment => frag.name.to_s}).value
        return false
      end
    end
    return true
  end
end
