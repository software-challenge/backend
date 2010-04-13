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
    RoundSlot.transaction do
      round.slots.each do |slot|
        return if slot.score.cause == "LEFT"
      end
      score.cause = "LEFT"
      self.qualification_changed = !self.qualification_changed
      score.save
      self.save!
    end
  end

  def requalify
    return if score.cause == "REGULAR"
    RoundSlot.transaction do
      score.cause = "REGULAR"
      self.qualification_changed = !self.qualification_changed
      score.save
      self.save!
    end
  end
end
