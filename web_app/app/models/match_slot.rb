class MatchSlot < ActiveRecord::Base
  validates_presence_of :match
  validates_presence_of :contestant

  belongs_to :match
  belongs_to :score, :dependent => :destroy

  has_many :round_slots, :dependent => :destroy
  has_many :round_scores, :through => :round_slots, :class_name => "Score", :source => :score
  has_many :rounds, :through => :round_slots

  acts_as_list :scope => :match_id

  belongs_to :client
  delegate :contestant, :to => :client

  def round_score_array
    self.round_scores.collect do |score|
      score.to_a
    end
  end

  def reset!
    score.destroy if score
  end

  def occupied?
    !!client
  end

  def cause_distribution
    returning Hash.new do |result|
      round_scores.all( :select => "cause, COUNT(*) AS count", :group => "cause" ).collect do |row|
        result[row[:cause] || "NIL"] = row[:count]
      end
    end
  end

  def name
    if occupied?
      contestant.name || "Unbenannt"
    else
      nil
    end
  end
end
