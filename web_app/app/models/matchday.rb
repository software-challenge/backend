require_dependency 'sandbox_helpers'

class Matchday < ActiveRecord::Base

  def do_validation?
    true
  end

  # named scopes
  named_scope :played, :conditions => "played_at IS NOT NULL"
  named_scope :not_played, :conditions => "played_at IS NULL"
  named_scope :published, :conditions => {:public => true}
  named_scope :trials, :conditions => {:trial => true}
  named_scope :without_trials, :conditions => {:trial => false}

  # validations
  #validates_presence_of :contest
  validates_presence_of :when, :if => :do_validation?
  validates_uniqueness_of :when, :scope => :contest_id, :if => :do_validation?

  validate do |record|
    #if not (record.contest.nil? ^ record.finale.nil?)
      #record.errors.add :matchday, "one and only one of contest and finale must be filled"
    #end

    return if (self.class == FinaleMatchday)

    if record.when_changed?
      # record.errors.add :when, "must be today or in the future" if record.when < Date.today
      
      if record.contest.matchdays.first(:conditions => ["matchdays.position > ? AND matchdays.when < ?", record.position, record.when])
        record.errors.add :when, "must not be after following matchdays"
      end
      if record.contest.matchdays.first(:conditions => ["matchdays.position < ? AND matchdays.when > ?", record.position, record.when])
        record.errors.add :when, "must not be before preceding matchdays"
      end
      unless record.moveable?
        record.errors.add :when, "can't be changed, since game is playing or was already played"
      end
    end
  end

  # associations
  has_many :matches, :class_name => "LeagueMatch", :dependent => :destroy, :as => :set
  has_many :slots, :class_name => "MatchdaySlot", :order => "position ASC", :dependent => :destroy
  belongs_to :contest
  belongs_to :finale
  belongs_to :job, :dependent => :destroy, :class_name => "Delayed::Job"
  has_many :mini_jobs, :through => :matches, :source => :job

  # delegates OR :through associations
  has_many :match_slots, :through => :slots

  delegate :game_definition, :to => :contest

  # acts
  #acts_as_list :scope => :contest_id
  acts_as_list :scope => 'contest_id=#{contest_id} AND type=\'Matchday\''

  def contestants
    contestants = []
    matches.each do |match|
      contestants.concat(match.contestants)
    end
    contestants
  end

  def played?
    !played_at.nil?
  end

  def moveable?
    !played? and !running?
  end

  def running?
    !job.nil? || !self.mini_jobs.empty?
  end

  def published?
    self.public
  end

  def reaggregate
    return false if self.running?
    Matchday.transaction do
      pub = self.public
      self.reset!
      self.reload
      self.matches.each do |match|
        match.after_round_played(nil)
      end
      self.public = pub
      save!
    end
    true
  end

  def perform_delayed!
    matches.each do |match|
      match.perform_delayed!
    end
  end
  
  def disqualifications
    disqualifications = {}
    self.matches.each do |match|
      disqualified_rounds = match.rounds.select{|r| r.has_disqualified_slot?}
      unless disqualified_rounds.empty? 
        disqualifications[match] = {:count => 0, :causes => []}
        disqualified_rounds.each do |disq_round|
          disqualifications[match][:causes] += disq_round.scores.collect{|score| score.cause}.select{|cause| cause != "REGULAR"}
          disqualifications[match][:count] += 1
        end
      end
    end
    return disqualifications
  end

  def has_disqualifications?
   self.matches.each do |match|
     match.rounds.each do |round|
       return true if round.has_disqualified_slot?
     end
   end
   false
  end

  def load_active_clients!(force_reload = false)
    matches.each do |match|
      match.slots.each do |league_slot|
        slot = league_slot.matchday_slot
        if (slot.client.nil? or force_reload)
          slot.client = slot.contestant.current_client
          slot.save!
        end
      end
    end
  end

  # Delayed::Job handler
  def perform
    matches.each do |match|
      match.perform
    end
    self.job_id = nil
    save!
  end

  def reset!(delete_games = false)
    raise "Can't reset while Job is running!" if running?
    
    Matchday.transaction do
      matches.each do |match|
        Match.benchmark("resetting match", Logger::DEBUG, false) do
          match.reset!(delete_games)
        end
      end

      self.played_at = nil
      self.public = false
      save!
    end
  end

  def job_logger
    SO_CHA_MANAGER_LOGGER
  end

  def client_played?(client)
    if played?
      (slots.find_by_client_id(client.id).nil? ? false : true)
    else
      false
    end
  end

  def match_for_client(client)
    slots.find_by_client_id(client.id).matches.first
  end

  # Callback (called by Match.perfom)
  def after_match_played(match)
    puts "Match was played"
    job_logger.info "Received after_match_played from #{match}"
    if all_matches_played?
      job_logger.info "All matches played"
      job_logger.info "Update scoretable"
      update_scoretable
      job_logger.info "Order scoretable"
      order_scoretable
      job_logger.info "Saving matchday"
      self.played_at = DateTime.now
      self.save!
      job_logger.info "Sending mail"
      Thread.new do
        begin
          EventMailer.deliver_on_matchday_played_notification(self) 
        ensure
          puts "Finished mail"
        end
      end
      job_logger.info "Matchday finished"
    end
  end

  def winners
    #raise "Matchdays have not all been played yet" unless contest.matchdays.all.find{|day| day.when > self.when}.nil?
    return slots.find_all{|slot| !slot.contestant.hidden? and slot.position <= 8}
  end

  def losers
    raise "Losers not available for #{self}"
  end

  def finished?
    contest.regular_phase_finished?
  end

  def rounds_done
    matches.collect{|match| match.rounds.find_all{|round| round.played?}}.flatten.count
  end

  def rounds_count
    matches.collect{|match| match.rounds}.flatten.count
  end

  def position_without_trials
    position - contest.matchdays.trials.count
  end


  protected

  def all_matches_played?(force_reload = true)
    self.matches(force_reload).first(:conditions => { :played_at => nil }).nil?
  end

  def order_scoretable
    definition_fragments = contest.game_definition.match_score.values
    joins = ["INNER JOIN scores AS order_scores ON order_scores.id = matchday_slots.score_id"]

    orders = []
    definition_fragments.each_with_index do |fragment, i|
      if fragment.ordering
        orders << "fragment_#{i}.value #{fragment.ordering.upcase}"
        joins << ("INNER JOIN score_fragments AS fragment_#{i} ON (fragment_#{i}.score_id = order_scores.id AND fragment_#{i}.fragment = '#{fragment.name.to_s}')")
      end
    end

    unless orders.empty?
      all_slots = slots(:reload).all(:order => orders.join(', '), :joins => joins.join(' '), :group => "matchday_slots.id")
      ranked_slots = all_slots.find_all{|slot| !slot.contestant.hidden?}
      highest_position = 0
      ranked_slots.each_with_index do |slot, i|
        writeable_slot = slots.find(slot.id)
        writeable_slot.position = i.next
        writeable_slot.save!
        highest_position = i.next
      end

      hidden_slots = all_slots.find_all{|slot| slot.contestant.hidden?}
      hidden_slots.each_with_index do |slot,i|
        writeable_slot = slots.find(slot.id)
        writeable_slot.position = highest_position + i.next
        writeable_slot.save!
      end
      
    else
      logger.warn "ORDER was empty - cannot order."
    end
  end

  def previous
    contest.matchdays.first(:conditions => ["position < ?", position], :order => "position DESC")
  end

  # Aggregates every matchday result for every contestant
  def update_scoretable
    slots.each do |slot|
      # For each contestant go through all earlier played matchdays
      # elements = [[1,0,0],[2,3,0],[3,0,0],[4,2,0]]
      elements = contest.matchdays(:reload).without_trials.all(:conditions => ["played_at IS NOT NULL AND position < ?", position]).collect do |day|
        # If there was a game on that day, match_slot will not be nil
        match_slot = day.match_slots(:reload).first(:conditions => ["matchday_slots.contestant_id = ?", slot.contestant.id])
        
        # Add result of that day to array
        if match_slot and match_slot.score
          match_slot.score.to_a
        else
          nil
        end
      end
      
      # if there was a game on current day, add this too
      if slot.match_slot and not trial
        elements << slot.match_slot.score.to_a
      end

      # Remove nil elements (days that contestant did not play)
      nil_count = elements.size - elements.nitems
      logger.warn "array contained #{nil_count} nil elements" unless nil_count.zero?
      elements.compact!
      
      # Now aggregate scores
      result = contest.game_definition.aggregate_matches(elements)

      slot.score ||= slot.build_score(:game_definition => contest[:game_definition], :score_type => "match_score")
      slot.score.set!(result)
      slot.save!
    end
  end

end
