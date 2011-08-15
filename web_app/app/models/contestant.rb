# represents a school
class Contestant < ActiveRecord::Base
  
  acts_as_authorization_object

  RANKINGS = %w{beginner advanced none}

  has_one :preliminary_contestant 
  has_one :school, :through => :preliminary_contestant
  belongs_to :season
  has_and_belongs_to_many :contests, :before_add => :is_no_duplicate_name_in_contest!
  has_many :ticket_contexts, :class_name => "Quassum::TicketContext", :as => "context"

  has_many :clients, :dependent => :destroy

  has_many :memberships, :dependent => :destroy
  has_many :people, :through => :memberships
  has_many :survey_tokens, :as => :token_owner
  has_many :slots, :class_name => "MatchdaySlot"
  has_many :matchdays, :through => :slots, :conditions => ['type = ?', "Matchday"]
  has_many :friendly_encounter_slots
  has_many :all_friendly_encounters, :through => :friendly_encounter_slots, :source => :friendly_encounter
  has_many :report_events, :class_name => "ContestantReportEvent", :dependent => :destroy, :order => "created_at DESC", :foreign_key => "param_int_1" 

  def friendly_matches_running
    friendly_encounters.collect{|enc| enc.mini_jobs}.flatten.reject{|m| m.nil?}.count
  end

  def has_hidden_friendly_encounters?
    friendly_encounter_slots.inject(false) {|val, x| val or x.hidden?}
  end

  def season
    return Season.find_by_id(season_id) if season_id
    return contests.map{|c| c.season}.compact.first
    nil
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
  validates_inclusion_of :ranking, :in => RANKINGS
  validates_uniqueness_of :name, :scope => :season_id, :if => :season

  validate do |record|
    if Contestant.all.find{|c| c.name == record.name and c.id != record.id and not (c.contests & record.contests).empty?}
      record.errors.add :contests, "may not have two Contestant with the same name"
    end
  end

  def is_no_duplicate_name_in_contest!(contest)
    raise "Name is already in use in this Contest!" if contest.contestants.to_ary.find{|c| c.name == self.name and c.id != self.id}
    true
  end

  attr_readonly :contests
  attr_protected :contests

  named_scope :without_testers, :conditions => { :tester => false }
  named_scope :for_contest, lambda{|c| {:joins => "join contestants_contests as jt on jt.contestant_id = contestants.id", :conditions => ["jt.contest_id = ?",c.id]}}
  named_scope :for_season, lambda{|s| {:conditions => ["season_id = ?",s.id]}}
  named_scope :visible, :conditions => { :hidden => false }
  named_scope :hidden, :conditions => { :hidden => true }

  #RANKINGS.each do |ranking|
  #  named_scope ("ranked_#{ranking}").to_sym, :conditions => {:ranking => ranking} 
  #end

  named_scope :ranked, :conditions => ["ranking != ? AND tester = ? AND hidden = ?", "none", "false", "false"]
  named_scope :unranked, :conditions => ["ranking == ?", "none"]

  def ranked?
    ["beginner", "advanced"].include? ranking and not disqualified
  end

  def worth_editing?
    not administrator? and ranked?
  end

  def rank_for_contest(contest)
    if contest.begun? and contests.include?(contest)
      contest.last_played_matchday.rank_for self
    else
      nil
    end
  end
  
  def matches
    contests.inject([]){|arr,cont| arr += cont.matches.with_contestant(self)}
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
      matchdays.each do |md|
        slot = md.slots.all.find{|s| s.contestant == self}
        if not slot.nil?
          slot.matches.each do |m|
            change_qualify_for_match m, :requalify
          end
        end
      end
      self.disqualified = false
      self.ranking = "beginner"
      self.save!
    end
    contests.each{|c| c.reaggregate!}
  end

  def disqualify
    raise "Team is already disqualified" if self.disqualified
    transaction do
      self.disqualified = true 
      matchdays.each do |md|
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
    contests.each{|c| c.reaggregate}
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

  def matches_including_free_days(contest)
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
    encounter = parent.friendly_encounters.create!(:context => parent)
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
  def find_open_requests(contest)
    if contest
      contest.friendly_encounters.collect{|enc| (enc.open? or enc.ready?) and (enc.open_for.nil? or enc.open_for == self)}
    else
      contests.map{|contest| contest.friendly_encounters.collect{|enc| (enc.open? or enc.ready?) and (enc.open_for.nil? or enc.open_for == self)}}.flatten
    end
  end

  # Are there open requests that I can accept or have accepted and not yet played?
  def has_open_friendly_requests_from_others?(contest = nil)
    friendly_requests_from_others(contest).find{|enc| enc.open? or enc.ready?} 
  end

  # Find requests that I can answer or already have answered 
  def friendly_requests_from_others(contest = nil)
    if contest 
      encs = contest.friendly_encounters.to_ary.find_all{|enc| enc.of_interest_for?(self) and not enc.contestants.first == self}
    else
      encs = contests.map{|contest| contest.friendly_encounters.to_ary.find_all{|enc| enc.of_interest_for?(self) and not enc.contestants.first == self}}.flatten
      encs += season.friendly_encounters.to_ary.find_all{|enc| enc.of_interest_for?(self) and not enc.contestants.first == self}.flatten if season
    end
    return encs
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

  def parent
    return season if season
    return contests.first unless contests.empty?
    nil
  end

  def possible_assignees
    people.map{|p| ["#{p.name} (#{p.roles.for(self).map{|r| r.to_s}.uniq.join(", ")})", "Person:#{p.id}"]}
  end


  protected
  before_destroy do |record|
    Role.for_authorizable(record).destroy_all
  end

end
