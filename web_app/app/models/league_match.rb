class LeagueMatch < Match
  has_many :slots,
    :class_name => "LeagueMatchSlot",
    :foreign_key => :match_id,
    :dependent => :destroy,
    :order => "position"

  alias :matchday :set
  alias :matchday= :set=
end