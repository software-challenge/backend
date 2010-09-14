# represents a school
class Contestant < ActiveRecord::Base

  RANKINGS = %w{beginner advanced none}

  belongs_to :contest

  has_many :clients

  has_many :memberships
  has_many :people, :through => :memberships

  has_many :slots, :class_name => "MatchdaySlot"
  has_many :matchdays, :through => :slots, :conditions => ['type = ?', "Matchday"]
  has_many :friendly_encounter_slots
  has_many :all_friendly_encounters, :through => :friendly_encounter_slots, :source => :friendly_encounter

  def friendly_matches_running
    friendly_encounters.collect{|enc| enc.mini_jobs}.flatten.reject{|m| m.nil?}.count
  end

  def has_hidden_friendly_encounters?
    friendly_encounter_slots.inject(false) {|val, x| val or x.hidden?}
  end

  def friendly_matches
    friendly_encounter_slots.collect(&:matches).reject{|m| m.nil? or m.empty?}.flatten
  end

  def friendly_matches_today
    friendly_encounters.find_all{|enc| not enc.played_at.nil? and enc.played_at.to_date == Date.today}.count
  end

  def may_play_another_friendly_match_today?
    ENV['FRIENDLY_GAMES_PER_DAY'].nil? or (self.friendly_matches_today + friendly_matches_running < ENV['FRIENDLY_GAMES_PER_DAY'].to_i) or has_smith?
  end

  belongs_to :current_client, :class_name => "Client"

  validates_presence_of :name, :location
  validates_uniqueness_of :name, :scope => :contest_id
  validates_uniqueness_of :tester, :scope => :contest_id, :if => :tester
  validates_inclusion_of :ranking, :in => RANKINGS

  attr_readonly :contest
  attr_protected :contest

  named_scope :without_testers, :conditions => { :tester => false }
  named_scope :for_contest, lambda { |c| {:conditions => { :contest_id => c.id }} }
  named_scope :visible, :conditions => { :hidden => false }

  #RANKINGS.each do |ranking|
  #  named_scope ("ranked_#{ranking}").to_sym, :conditions => {:ranking => ranking} 
  #end

  named_scope :ranked, :conditions => ["ranking != ?", "none"]
  named_scope :unranked, :conditions => ["ranking == ?", "none"]

  def ranked?
    ["beginner", "advanced"].include? ranking and not disqualified
  end

  def worth_editing?
    not administrator? and ranked?
  end
  
  def matches
    contest.matches.with_contestant(self)
  end

  def change_qualify_for_match(match, change)
    raise "Possible changes are :disqualify and :requalify" unless [:requalify, :disqualify].include?(change)
    cont_index = match.contestants.index(self)
    match.rounds.each do |r|
      r.scores.each do |s|
        s.fragments.each do |f|
          if change == :disqualify
            f.adjust_to_worst
          elsif change == :requalify
            f.adjusted_value = nil
            f.save!
          end
        end
      end
    end
    match.permutated_round_slots.each do |slots|
      slot = slots[cont_index]
      if slot.round.played?
        s = slot.score
        if change == :disqualify
          s.adjusted_cause = "DISQUALIFIED"
        elsif change == :requalify
          s.adjusted_cause = nil
        end
        s.save!
      end
    end
  end

  def requalify
    raise "Team is not disqualified" unless self.disqualified
    transaction do
      contest.matchdays.each do |md|
        slot = md.slots.all.find{|s| s.contestant == self}
        if not slot.nil?
          slot.matches.each do |m|
            change_qualify_for_match m, :requalify
          end
        end
      end
      self.disqualified = false
      self.save!
    end
    contest.reaggregate!
  end

  def disqualify
    raise "Team is already disqualified" if self.disqualified
    transaction do
      self.disqualified = true 
      contest.matchdays.each do |md|
        slot = md.slots.all.find{|s| s.contestant == self}
        if not slot.nil?
          slot.matches.each do |m|
            change_qualify_for_match m, :disqualify
          end
        end
      end
      self.ranking = "none"
      self.save! 
    end  
    contest.reaggregate 
  end

  def remove_from_matchdays
    # NOTE: Don't use this, as it deletes the matches irreversibly
    # Matchday.transaction do
    #  contest.matchdays.each do |md|
    #    slot = md.slots.all.find{|s| s.contestant == self}
    #    if not slot.nil?
    #      slot.matches.each do |m|
    #        m.contestants.each do |c|
    #          c.slots.all.find{|s| s.matchday == md}.destroy
    #        end
    #        m.destroy
    #      end
    #    end
    #  end
    # end
    # contest.reaggregate
  end

  def matches_including_free_days
    matches = []
    contest.matchdays.each do |matchday|
      match = self.matches.to_ary.find{|match| match.matchday == matchday}
      if !match.blank?
        matches << match
      else
        matches << LeagueMatch.new(:matchday => matchday)
      end
    end
    return matches
  end

  def has_running_tests?
    !clients.running.count.zero?
  end

  def open_friendly_encounter_request(hash = {})
    raise ":to has to be specified" if not hash[:to]
    con = (hash[:to] == :all ? nil : hash[:to])
    encounter = contest.friendly_encounters.create!(:contest => contest)
    encounter.slots.create(:contestant => self)
    encounter.open_for = con
    
    encounter.save!
    encounter
  end

  def friendly_encounters
    if @contest.nil?
      all_friendly_encounters
    else
      all_friendly_encounters.for_contest(@contest)
    end
  end

  # Find all requests that are open for all or exclusively for me
  def find_open_requests
    contest.friendly_encounters.collect{|enc| (enc.open? or enc.ready?) and (enc.open_for.nil? or enc.open_for == self)}
  end

  # Are there open requests that I can accept or have accepted and not yet played?
  def has_open_friendly_requests_from_others?
    friendly_requests_from_others.find{|enc| enc.open? or enc.ready?}
  end

  # Find requests that I can answer or already have answered 
  def friendly_requests_from_others
    contest.friendly_encounters.to_ary.find_all{|enc| enc.of_interest_for?(self) and not enc.contestants.first == self}
  end

  # Find requests that are opened by me
  def friendly_requests
    friendly_encounters.find_all{|enc| enc.contestants.first == self}
  end

  def will_accept_friendly_requests?
    has_smith?
  end

  def has_smith?
    !ENV['MR_SMITH'].nil? and !memberships.to_ary.find{|m| m.person.email == ENV['MR_SMITH']}.nil?
  end

end
