class FriendlyEncounterSlot < ActiveRecord::Base
  validates_presence_of :contestant
  validates_presence_of :friendly_encounter

  belongs_to :friendly_encounter
  belongs_to :contestant
  belongs_to :client
  belongs_to :score, :dependent => :destroy

  has_one :match_slot, :class_name => "FriendlyMatchSlot", :dependent => :destroy, :foreign_key => "matchday_slot_id"
  has_many :matches, :through => :match_slot

  def hidden?
    self.hidden
  end
end
