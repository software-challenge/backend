class Round < ActiveRecord::Base
  validates_presence_of :match

  belongs_to :match
  has_many :slots, :class_name => "RoundSlot", :dependent => :destroy, :order => "position"
  has_many :scores, :through => :slots

  def played?; played_at; end

  def perform
    update_scores!([[1,0,0], [-1,0,0]])
  end

  def reset!
    slots.each do |slot|
      slot.reset!
    end

    self.played_at = nil
    save!
  end

  def update_scores!(new_scores)
    raise "new_scores must not be nil" unless new_scores
    raise "new_scores (#{new_scores.count}) must have same size as there are slots (#{slots.count})" if new_scores.count != slots.count
    
    Round.transaction do
      slots.each_with_index do |slot,index|
        score = slot.score
        unless score
          score = slot.build_score(:definition => match.matchday.contest.match_score_definition)
        end
        slot.score.set!(new_scores[index])
        slot.save!
      end
      
      self.played_at = DateTime.now
    end
    
    self.match.after_round_played(self)
  end
end
