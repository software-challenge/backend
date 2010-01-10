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
end