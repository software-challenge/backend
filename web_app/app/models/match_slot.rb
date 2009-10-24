class MatchSlot < ActiveRecord::Base
  belongs_to :match
  belongs_to :contestant, :polymorphic => true
  belongs_to :score

  has_many :round_slots
  has_many :round_scores, :through => :round_slots, :class_name => "Score", :source => :score

  acts_as_list :scope => :match_id

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
