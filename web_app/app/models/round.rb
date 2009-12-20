require 'sandbox'

class Round < ActiveRecord::Base
  validates_presence_of :match

  belongs_to :match
  has_many :slots, :class_name => "RoundSlot", :dependent => :destroy, :order => "position"
  has_many :scores, :through => :slots

  has_attached_file :replay

  def played?; played_at; end

  def perform
    x, y = rand(10), rand(10)
    a, b = 0, 0

    if x > y
      a = 1
    elsif x < y
      b = 1
    end

    t1, t2 = rand(100), rand(100)

    update_scores!([[a,x,t1], [b,y,t2]])
  end

  def reset!
    slots.each do |slot|
      slot.reset!
    end

    self.played_at = nil
    save!
  end

  def score_definition
    match.matchday.contest.round_score_definition
  end

  def update_scores!(new_scores)
    raise "new_scores must not be nil" unless new_scores
    raise "new_scores (#{new_scores.count}) must have same size as there are slots (#{slots.count})" if new_scores.count != slots.count
    
    Round.transaction do
      slots.each_with_index do |slot,index|
        unless slot.score
          slot.score = slot.build_score(:definition => score_definition)
        end
        slot.score.set!(new_scores[index])
        slot.save!
      end
      
      self.played_at = DateTime.now
      save!
    end
    
    self.match.after_round_played(self)
  end
end
