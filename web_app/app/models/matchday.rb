require 'sandbox'

class Matchday < ActiveRecord::Base
  validates_presence_of :order
  validates_presence_of :contest
  validates_presence_of :when
  validates_uniqueness_of :order, :scope => :contest_id

  has_many :matches, :dependent => :destroy, :as => :set
  belongs_to :contest
  belongs_to :job, :class_name => "Delayed::Job"

  def position
    self.contest.matchdays.count(:conditions => ["matchdays.when < ? OR (matchdays.when = ? AND matchdays.order < ?)", self.when, self.when, self.order]) + 1
  end

  def played?
    !played_at.nil?
  end

  def running?
    !job.nil?
  end

  # Delayed::Job handler
  def perform
    matches.each do |match|
      match.perform
    end
  end

  def reset!
    raise "Can't reset while Job is running!" if running?
    
    Matchday.transaction do
      self.matches.each do |match|
        match.played_at = nil
        match.save!
      end

      self.played_at = nil
      save!
    end
  end

  # Callback (called by Match.perfom)
  def after_match_played(sender = nil)
    logger.info "Received after_match_played from #{sender}"
    if all_matches_played?(true)
      update_scoretable
      self.played_at = DateTime.now
      self.save!
    end
  end

  protected

  def all_matches_played?(force_reload = false)
    self.matches(force_reload).first(:conditions => { :played_at => nil }).nil?
  end

  def update_scoretable
    sandbox = Sandbox.new("sum_all(elements)")
    mod = Module.new do
      define_method :assert_size do |rows|
        if rows.empty?
          true
        else
          default_size = rows.first.size
          rows.each do |row|
            raise "row sizes didn't match" unless row.size == default_size
          end
        end
      end

      define_method :sum_all do |rows|
        if rows.empty?
          []
        else
          assert_size rows
          width = rows.first.size
          result = []
          width.times do |i|
            result << rows.inject(0) { |sum,x| sum + x[i] }
          end
          result
        end
      end

    end
    sandbox.extend mod

    result = sandbox.invoke(:locals => {:elements => [[1,0,0],[2,3,0],[3,0,0],[4,2,0]]})
  end
end
