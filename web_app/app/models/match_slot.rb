class MatchSlot < ActiveRecord::Base
  belongs_to :match
  belongs_to :contestant, :polymorphic => true
  belongs_to :score

  acts_as_list :scope => :match_id

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
