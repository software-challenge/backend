class LeagueMatch < Match
  has_many :slots,
    :class_name => "LeagueMatchSlot",
    :foreign_key => :match_id,
    :dependent => :destroy,
    :order => "position"

  alias :matchday :set
  alias :matchday= :set=

  def contestants=(contestants)
    Match.transaction do
      contestants.each do |contestant|
        if contestant
          slots.create!(:matchday_slot => matchday.slots.first(:conditions => { :contestant_id => contestant.id }))
        else
          slots.create!
        end
      end
      create_rounds!
    end
  end

  def priority
    Match::MEDIUM_PRIORITY
  end


  def load_active_clients(force_reload = false)
    slots.each do |league_slot|
      slot = league_slot.matchday_slot
      if (slot.client.nil? or force_reload)
        slot.client = slot.contestant.current_client
        slot.save!
      end    
    end
  end
end
