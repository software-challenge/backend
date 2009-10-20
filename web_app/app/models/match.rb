class Match < ActiveRecord::Base
  has_many :slots, :dependent => :destroy, :class_name => "MatchSlot"
  
  belongs_to :set, :polymorphic => true

  alias :matchday :set
  alias :matchday= :set=

  def played?
    !played_at.nil?
  end
  
  # Delayed::Job handler
  def perform
    unless played?
      run_match
      self.played_at = DateTime.now
      self.save!
    end

    if set and set.respond_to? :after_match_played
      set.after_match_played self
    end
  end

  protected

  def run_match
    # perform the match
    sleep 1
  end
end
