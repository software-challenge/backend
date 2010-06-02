require_dependency 'sandbox_helpers'

class Match < ActiveRecord::Base

  HIGH_PRIORITY = 10 # finals
  MEDIUM_PRIORITY = 5
  LOW_PRIORITY = 0 # client-tests

  named_scope(:with_contestant, lambda do |contestant|
      { :joins => "INNER JOIN match_slots ms ON ms.match_id = matches.id " +
          "INNER JOIN matchday_slots mds ON ms.matchday_slot_id = mds.id" ,
        :conditions => ["mds.contestant_id = ?", contestant.id]}
    end)

  validates_presence_of :type
  validates_presence_of :set

  belongs_to :set, :polymorphic => true
  belongs_to :job, :dependent => :destroy, :class_name => "Delayed::Job"

  has_many :slots, :class_name => "MatchSlot", :dependent => :destroy, :order => "position"
  has_many :rounds, :dependent => :destroy
  has_many :scores, :through => :slots, :order => "POSITION ASC"

  delegate :game_definition, :to => :set
  delegate :contest, :to => :set

  def contestants
    slots.collect{|slot| slot.contestant}
  end

  def running?
    !!job
  end

  def played?
    !played_at.nil?
  end

  def result
    result = scores.all
    return nil if result.empty?
    
    result.collect do |score|
      score.to_a_with_precision
    end.transpose
  end

  def main_result
    result = scores.all
    return nil if result.empty?

    main_field = contest.game_definition.match_score_main_field
    result.collect do |score|
      score_fragment = score.fragments.first(:conditions => { :fragment => main_field.to_s })
      raise "main score_fragment #{main_field} not found" unless score_fragment
      score_fragment.value_with_precision
    end
  end

  def contest
    set.contest
  end

  def score_definition
    contest.match_score_definition
  end

  def perform_delayed!
    job_id = Delayed::Job.enqueue self, priority
    self.job = Delayed::Job.find(job_id)
    save!
  end
  
  # Delayed::Job handler
  def perform
    unless played?
      run_match
    end
  end

  def priority
    MEDIUM_PRIORITY
  end

  def reset!(delete_games = false)
    if delete_games
      rounds.each do |round|
        round.reset!
      end
    end

    slots.each do |slot|
      slot.reset!
    end

    self.played_at = nil
    self.matchday.played_at = nil
    self.matchday.save!
    save!
  end

  def after_round_played(round)
    logger.info "Received after_round_played from #{round}"

    if all_rounds_played?
      logger.info "All rounds finished for match #{self}!"
     
      logger.info "Updating scoretable." 
      update_scoretable
      self.played_at = DateTime.now
      self.save!
      
      if set and set.respond_to? :after_match_played
        set.after_match_played self
      end
    else
      logger.info "Not all rounds are finished yet."
    end
  end


  protected

  def create_rounds!(total_rounds = nil)
    raise "already has rounds" unless rounds.empty?
    total_rounds ||= game_definition.league.rounds
    
    round_count = 0
    while round_count < total_rounds
      (0...slots.count).to_a.permute do |permutation|
        round_count = round_count + 1
        round = self.rounds.create!
        permutation.each do |slot_index|
          round.slots.create!(:match_slot => slots[slot_index])
        end
        break if round_count >= game_definition.league.rounds
      end
    end
  end

  def update_scoretable
    slots.each do |slot|
      other_slots = slots.reject{ |item| item == slot }
      # NOTE: it is expected that the round scores are always in the order of the rounds
      others = other_slots.collect{ |other_slot| other_slot.round_score_array_with_causes }
      mine = slot.round_score_array_with_causes

      result = contest.game_definition.aggregate_rounds(mine, others)

      slot.score ||= slot.build_score(:game_definition => contest[:game_definition], :score_type => "match_score")
      slot.score.set!(result)
      slot.save!
    end
    logger.info "Scoretable updated for match #{self}"
  end

  def slot_for(client)
    slots.first( :conditions => { :client_id => client.id } )
  end

  def all_rounds_played?(force_reload = true)
    self.rounds(force_reload).first(:conditions => { :played_at => nil }).nil?
  end

  def run_match()
    rounds.each do |round|
      if not round.played?
        round.perform
      end
    end
  end
end
