require_dependency 'sandbox_helpers'

class Match < ActiveRecord::Base
  named_scope(:with_contestant, lambda do |contestant|
      { :joins => "INNER JOIN match_slots ms ON ms.match_id = matches.id " +
          "INNER JOIN matchday_slots mds ON ms.matchday_slot_id = mds.id" ,
        :conditions => ["mds.contestant_id = ?", contestant.id]}
    end)

  validates_presence_of :set

  belongs_to :set, :polymorphic => true
  belongs_to :job, :dependent => :destroy, :class_name => "Delayed::Job"

  has_many :slots, :class_name => "MatchSlot", :dependent => :destroy, :order => "position"
  has_many :rounds, :dependent => :destroy
  has_many :scores, :through => :slots

  alias :matchday :set
  alias :matchday= :set=
  def played?; played_at; end
  def running?; !job.nil?; end

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

    result.collect do |score|
      score.fragments.first(:joins => "INNER JOIN score_definition_fragments sdf ON sdf.id = score_fragments.definition_id",
        :order => "sdf.main DESC, sdf.position ASC").value_with_precision
    end
  end

  def contest
    set.contest
  end

  def score_definition
    contest.match_score_definition
  end

  def perform_delayed!
    job_id = Delayed::Job.enqueue self
    self.job = Delayed::Job.find(job_id)
    save!
  end
  
  # Delayed::Job handler
  def perform
    unless played?
      run_match
    end
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
    save!
  end

  def after_round_played(round)
    logger.info "received after_round_played from #{round}"

    if all_rounds_played?
      logger.info "all rounds finished!"
      
      update_scoretable
      self.played_at = DateTime.now
      self.save!
      
      if set and set.respond_to? :after_match_played
        set.after_match_played self
      end
    else
      logger.info "not all rounds are finished yet"
    end
  end

  protected

  def update_scoretable
    slots.each do |slot|
      result = nil
      me = slot.contestant
      rounds = slot.rounds.all
      
      other_slots = slots.reject{|item| item == slot}
      others = other_slots.collect{|other_slot| other_slot.round_score_array}
      mine = slot.round_score_array
      
      # add some dynamic methods
      contest.game_definition.round_score.values.each_with_index do |field, i|
        others.collect do |other|
          other.each do |score|
            score.define_singleton_method field.name do
              score[i]
            end
          end
        end
        mine.each do |my|
          my.define_singleton_method field.name do
            my[i]
          end
        end
      end
      
      result = []
      contest.game_definition.match_score.each do |k,v|
        if v.callback
          result << v.callback.call(mine, others)
        end
      end

      slot.score ||= slot.build_score
      slot.score.set!(result)
      slot.save!
    end
  end

  def all_rounds_played?(force_reload = true)
    self.rounds(force_reload).first(:conditions => { :played_at => nil }).nil?
  end

  def run_match
    rounds.each do |round|
      round.perform
    end
  end
end
