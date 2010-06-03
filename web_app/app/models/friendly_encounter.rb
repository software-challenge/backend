class FriendlyEncounter < ActiveRecord::Base
  has_one :friendly_match, :class_name => "FriendlyMatch", :dependent => :destroy, :as => :set
  has_many :slots, :class_name => "FriendlyEncounterSlot", :dependent => :destroy
  belongs_to :contest
  belongs_to :job, :dependent => :destroy, :class_name => "Delayed::Job"
  has_many :mini_jobs, :through => :friendly_match, :source => :job

  has_many :match_slots, :through => :slots
  delegate :game_definition, :to => :contest

  belongs_to :open_for, :class_name => "Contestant"
  has_many :contestants, :through => :slots
  
  def date
    self.created_at
  end

  def slot_for(con)
    slots.to_ary.find{|slot| slot.contestant == con}
  end
  
  def hidden_for?(con)
    slot = slot_for(con)
    !slot.nil? and slot.hidden?
  end

  def playable?
    self.contestants.inject(true) {|val, con| val && con.may_play_another_friendly_match_today?}
  end

  def published?
    true
  end

  def played?
    !played_at.nil?
  end

  def running?
    !job.nil? || !self.mini_jobs.empty?
  end

  def perform_delayed!
    raise "Friendly encounter not ready yet" if not ready?
    friendly_match.perform_delayed!
  end

  def load_active_clients!(force_reload = false)
      friendly_match.slots.each do |friendly_match_slot|
        slot = friendly_match_slot.friendly_encounter_slot
        if (slot.client.nil? or force_reload)
          slot.client = slot.contestant.current_client
          slot.save!
        end
      end
  end

  def full?
    contestants.count == game_definition.players
  end

  %w{open ready rejected}.each do |k|
    define_method "#{k}?" do
      status == k
    end
  end 

  def status
    return "played" if self.played?
    return "rejected" if self.rejected
    return "running" if running?
    return "open" if not full? and not self.rejected
    return "ready" if full? and not self.rejected and not running? and not played?
    return "unknown"
  end

  def play!
    if ready? and not running?
      prepare
      load_active_clients!
      perform_delayed!
    end
  end

  def add_contestant(con)
    raise "Game not open" if not open?
    raise "Contestant already in here" if contestants.include?(con)
    slots.create!(:contestant => con)
    save!
    if ready? and playable?
      play!
    end
  end

  def prepare
    raise "Friendly encounter not ready yet" if not ready?
    friendly_match.destroy if not friendly_match.nil?
    friendly_match = FriendlyMatch.new(:friendly_encounter => self)
    friendly_match.save!
    friendly_match.contestants = contestants.to_ary
    self
    self.reload
  end

  def rejectable_by?(per)
    not open_for.nil? and per.has_role_for? open_for
  end

  def acceptable_by?(per)
    per.has_role_for? open_for
  end

  def startable_by?(per)
    contestants.each do |con|
      return true if per.has_role_for? con
    end
    return false
  end

  def of_interest_for?(con)
    (open_for.nil? and open?) or open_for == con or contestants.include?(con)
  end

  def result
    return nil if not friendly_match
    friendly_match.result
  end

  def main_result
    return nil if not friendly_match
    friendly_match.main_result
  end

  def after_match_played(match)
    if match == friendly_match
      self.played_at = DateTime.now
      self.save!
    end
  end

  def reject
    self.rejected = true
    self.save!
  end
end
