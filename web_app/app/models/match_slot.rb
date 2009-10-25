class MatchSlot < ActiveRecord::Base
  validates_presence_of :match
  validates_presence_of :matchday_slot

  belongs_to :match
  belongs_to :matchday_slot
  belongs_to :score, :dependent => :destroy

  has_many :round_slots, :dependent => :destroy
  has_many :round_scores, :through => :round_slots, :class_name => "Score", :source => :score

  acts_as_list :scope => :match_id
  delegate :contestant, :to => :matchday_slot

  def round_score_array
    self.round_scores.collect do |score|
      score.to_a
    end
  end

  def reset!
    score.destroy if score
  end

  def name
    if contestant
      contestant.name || "Unbenannt"
    else
      nil
    end
  end
end
