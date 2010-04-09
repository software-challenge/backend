# represents a school
class Contestant < ActiveRecord::Base

  belongs_to :contest

  has_many :clients

  has_many :memberships
  has_many :people, :through => :memberships

  has_many :slots, :class_name => "MatchdaySlot"
  has_many :matchdays, :through => :slots

  belongs_to :current_client, :class_name => "Client"

  validates_presence_of :name, :location
  validates_uniqueness_of :name, :scope => :contest_id
  validates_uniqueness_of :tester, :scope => :contest_id, :if => :tester

  attr_readonly :contest
  attr_protected :contest

  named_scope :without_testers, :conditions => { :tester => false }
  named_scope :for_contest, lambda { |c| {:conditions => { :contest_id => c.id }} }
  named_scope :visible, :conditions => { :hidden => false }

  def matches
    contest.matches.with_contestant(self)
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

end
