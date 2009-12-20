class RoundSlot < ActiveRecord::Base
  validates_presence_of :round
  validates_presence_of :match_slot

  belongs_to :round
  belongs_to :match_slot
  belongs_to :score, :dependent => :destroy

  acts_as_list :scope => :round_id

  delegate :name, :matchday_slot, :to => :match_slot
  delegate :client, :contestant, :to => :matchday_slot

  def reset!
    score.destroy if score
  end

  def ingame_name
    name.parameterize
  end
end
