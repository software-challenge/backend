class Match < ActiveRecord::Base
  validates_presence_of :set

  belongs_to :set, :polymorphic => true
  has_many :slots, :class_name => "MatchSlot", :dependent => :destroy, :order => "position"
  has_many :rounds, :dependent => :destroy
  has_many :scores, :through => :slots

  alias :matchday :set
  alias :matchday= :set=
  def played?; played_at; end

  def result
    result = scores.all
    return nil if result.empty?
    result
  end

  def score_definition
    set.match_score_definition
  end

  alias :round_score_definition :score_definition
  
  # Delayed::Job handler
  def perform
    unless played?
      run_match
    end
  end

  def reset!
    rounds.each do |round|
      round.reset!
    end

    slots.each do |slot|
      slot.reset!
    end

    self.played_at = nil
    save!
  end

  def after_round_played(round)
    if all_rounds_played?
      self.played_at = DateTime.now
      self.save!
      
      if set and set.respond_to? :after_match_played
        set.after_match_played self
      end
    end
  end

  protected

  def all_rounds_played?(force_reload = true)
    self.rounds(force_reload).first(:conditions => { :played_at => nil }).nil?
  end

  def run_match
    rounds.each do |round|
      round.perform
    end
  end
end
