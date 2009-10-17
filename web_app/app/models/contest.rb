class Contest < ActiveRecord::Base
  has_many :contestants
  has_many :matchdays

  def to_param
    "#{id}-#{name.dasherize}"
  end

  def refresh_matchdays!
    Contest.transaction do
      matchdays.destroy_all

      next_date = Date.today
      generate_matchdays.each_with_index do |pairs,day|
        matches = pairs.collect do |contestants|
          match_slots = contestants.collect do |contestant|
            MatchSlot.new(:contestant => contestant)
          end
          Match.new(:slots => match_slots)
        end
        Matchday.new(:contest => self, :matches => matches, :order => day, :when => next_date).save!
        next_date += 1
      end
    end
  end

  protected

  # generates all matchdays (round-robin tournament)
  # NOTE: (later) how about swiss-system instead of round-robin
  def generate_matchdays
    result = []
    list = contestants.all
    list << nil if list.size.odd?

    rounds = list.size - 1
    half_size = list.size / 2

    relist = list.clone
    relist.delete_at 0

    rounds.times do |round|
      schedule = []
      first = relist[round % relist.size]
      second = list.first
      
      schedule << [first, second]

      (1...half_size).each do |i|
        first_index = (round + i) % relist.size
        second_index = (round + relist.size - i) % relist.size
        schedule << [relist[first_index], relist[second_index]]
      end

      result << schedule
    end

    result
  end
end
