class RoundSlot < ActiveRecord::Base
  validates_presence_of :round
  validates_presence_of :match_slot

  belongs_to :round
  belongs_to :match_slot
  belongs_to :score, :dependent => :destroy

  acts_as_list :scope => :round_id

  delegate :name, :client, :contestant, :to => :match_slot

  def reset!
    score.destroy if score
  end

  def ingame_name
    name.parameterize
  end

  def disqualify
    # Make sure that nobody else has already been disqualified in this round
    round.slots.each do |slot|
      return if slot.score.cause == "LEFT"
    end
    score.cause = "LEFT"
    score.save
  end

  def requalify
    score.cause = "REGULAR"
    score.save
  end
end
