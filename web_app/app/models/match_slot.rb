class MatchSlot < ActiveRecord::Base
  belongs_to :contestant, :polymorphic => true
  belongs_to :match

  def name
    if contestant
      contestant.name || "Unbenannt"
    else
      nil
    end
  end
end
