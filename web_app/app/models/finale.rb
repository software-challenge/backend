require_dependency 'sandbox_helpers'

class Finale < ActiveRecord::Base

  belongs_to :contest
  belongs_to :job, :dependent => :destroy, :class_name => "Delayed::Job"
  
  has_many :days, :class_name => "FinaleMatchday", :dependent => :destroy, :foreign_key => "contest_id"

  delegate :game_definition, :to => :contest

  def mini_jobs
    days.collect(&:mini_jobs).flatten
  end

  def publish
    if finished?
      self.published = true
      days.each do |day|
        day.public = true
        day.save!
      end
      self.save!
      self.reload
    end
  end

  def hide
    if published?
      self.published = false
      self.save!
      self.reload
    end
  end

  def published?
    published
  end

  def matches
    days.collect(&:matches).flatten
  end

  def slots
    matches.collect(&:slots).flatten
  end

  def matchdays_running
    days.all.find_all{|day| day.running?}
  end

  def matchday_running?
    !matchdays_running.empty?
  end

  def find_day(dayname)
    return contest.last_played_matchday if dayname == :contest
    day = days.find(:first, :conditions => {:name => dayname.to_s})
    raise "Day with name #{dayname} does not exists" if day.nil?
    return day
  end

  def day_settings(dayname)
    contest.game_definition.day_settings_for(dayname)
  end

  def dependencies_for(dayname)
    return (day_settings(dayname)[:depends] ||= [])
  end

  def dependent_on?(day, on) 
    dependencies_for(day).include?(on)
  end 

  def days_dependent_on(dayname)
    days = []
    game_definition.final_days.each do |name,options|
      if dependent_on?(name, dayname)
        days << name
      end
    end
    days
  end

  def days_depend_on?(dayname)
    !days_dependent_on(dayname).empty?
  end

  def dependencies_met_for?(dayname)
    return false if not contest.regular_phase_finished?
    
    dependencies = dependencies_for(dayname)
    return true if dependencies.nil?

    dependencies.each do |dependency|
      depday = find_day(dependency)
      return false if not depday.played?
    end
    true
  end

  def source_for_day(dayname)
    day_settings(dayname)[:from]
  end

  def source_slots_for(dayname)
    settings = day_settings(dayname)
    use = settings[:use]
    from = find_day(settings[:from])
    fromSettings = day_settings(settings[:from])
    if use.class == Hash
      num = use[:best]
      return from.slots.find_all{|slot| !slot.contestant.hidden? and slot.position <= num}
    else
      if use == :winners 
        return from.winners(:multiple => fromSettings[:multipleWinners]).flatten
      else 
        return from.losers(:multiple => fromSettings[:multipleWinners]).flatten
      end
    end
  end

  def prepare_day(dayname)
    raise "Dependencies for day #{dayname} not met" if not dependencies_met_for?(dayname)

    # Find day and previous day
    day = find_day(dayname)
    daysettings = day_settings(dayname)
    
    sourcedayname = daysettings[:from]
    sourceday = find_day(sourcedayname)

    raise "#{dayname} has already been initialized!" if not day.matches.empty?
    raise "#{sourcedayname} has not been played yet!" if sourceday.nil? or not sourceday.played?

    Contest.transaction do
      slots = source_slots_for(dayname)
      raise "Number of slots is required to be even!" if not slots.count.even?

      slots.each_with_index do |slot,i|
        day.slots.create!(:contestant => slot.contestant)
      end

      pairs = create_pairs( (0..(slots.count - 1)), (not daysettings[:reorder_slots].nil? and daysettings[:reorder_slots]) )
      pairs.each do |a,b|
        match = day.matches.create!
        cons = [slots[a].contestant, slots[b].contestant]
        match.contestants = cons
      end
    end
    day
  end

  def create_pairs(range, reorder = false)
    pairs = []
    
    if reorder
      range1 = (range.first..((range.count / 2) - 1)).entries
      range2 = ((range.count / 2)..(range.count - 1)).entries.reverse
      while not range1.empty?
        pairs << [range1.first, range2.first]
        range1.delete_at 0
        range2.delete_at 0
        range1.reverse!
        range2.reverse!
      end
    else
      range.step(2).each do |i|
        pairs << [i, i+1]
      end
    end
    return pairs
  end

  def day_deletable?(dayname)
    return false if published?
    dependent_days = days_dependent_on(dayname)
    dependent_days.each do |depdayname|
      depday = find_day(depdayname)
      return false unless depday.matches.empty?
    end 
    true
  end

  def day_playable?(daytype)
    dependencies_met_for? daytype
  end

  def after_matchday_played(matchday)
    logger.info "Finale Matchday played: #{matchday}"
    matchday.job = nil
    matchday.save!
  end

  def play_all
    job_id = Delayed::Job.enqueue self, Match.const_get(:HIGH_PRIORITY)
    self.job = Delayed::Job.find(job_id)
    save!
  end

  def runnable?
    contest.regular_phase_finished?
  end

  def running?
    !job.nil? || !mini_jobs.empty? || matchday_running?
  end

  def started?
    return (not days.to_ary.find{|day| day.prepared?}.nil? or not job.nil?)
  end

  def finished?
    days.each do |day|
      return false if not day.played?   
    end
    true
  end

  def winner
    return nil if not finished?
    lastdayname = game_definition.final_days.sort_by{|d| d[1][:order]}.last
    lastday = find_day(lastdayname)
    return lastday.winners
  end

  def has_editable_day?
    return (not days.to_ary.find{|day| day_settings(day.name.to_sym)[:editable] and day.prepared? and not day.played? and not day.running?}.nil?)
  end

  def has_published_lineup?
    return (not days.to_ary.find{|day| day.prepared? and day.published? and day_settings(day.name.to_sym)[:lineup_publishable]}.nil?)
  end

  def ranking
    return nil if not finished?
    ranks = {}
    final_days = game_definition.final_days
    rankSetting = 1
    actualRank = 1
    done = false
    while not done
      dayentry = final_days.find{|day| not day[1][:ranking].nil? and not day[1][:ranking][rankSetting].nil?}
      unless dayentry.nil?
        dayname = dayentry[0]
        daysettings = dayentry[1]
        day = find_day(dayname)
        ranks[actualRank] = (daysettings[:ranking][rankSetting] == :winners ? day.winners : day.losers)
        actualRank += ranks[actualRank].count
        rankSetting += 1
      else
        done = true
      end
    end
    ranks
  end

  def perform
    self.reload
    days = contest.game_definition.final_days
    days.sort_by{|day| day[1][:order]}.each do |row|
      setting = row[1]
      name = row[0]
      #if setting[:show] > 0
        day = find_day(name)
        if day.matches.empty?
          day = prepare_day(name) 
        end
        if not day.played?
          day.job = job
          day.load_active_clients!
          day.save!
          day.perform
          while day.running?
            day.reload
          end
        end
      #end
    end
  end

  def export
    exp = {
      'settings' => [{
        'gameUID' => [game_definition.plugin_guid],
        'gameName' => [contest.game_name] }],
      'finalStep' => []
    }

    game_definition.final_days.each do |dayname,settings|
      day = find_day(dayname)
      dayXML = {
        'order' => settings[:order],
        'name' => settings[:name].to_s,
        'use' => settings[:use].to_s,
        'from' => settings[:from].to_s,
        'match' => []
      }
      day.matches.each do |match|
        matchXML = 
          {
            'players' => {'contestant' => []},
            'rounds' => {'round' => []},
            'winners' => {'contestant' => []},
            'losers' => {'contestant' => []}
          }
        match.contestants.each do |contestant|
          matchXML['players']['contestant'] << {
            'name' => contestant.name,
            'location' => contestant.location
          }
        end
        match.rounds.each do |round|
          matchXML['rounds']['round'] << {
            'replay' => [round.replay_file_name],
            'winner' => [round.winner.name] 
          }
        end
        match.winner.each do |slot|
          matchXML['winners']['contestant'] << {
            'name' => slot.contestant.name,
            'location' => slot.contestant.location
          }
        end
        match.loser.each do |slot|
          matchXML['losers']['contestant'] << {
            'name' => slot.contestant.name,
            'location' => slot.contestant.location
          }
        end
        dayXML['match'] << matchXML
      end
      exp['finalStep'] << dayXML

      rankingXML = {'rank' => []}
      self.ranking.sort.each do |rank,slots|
        rankXML = {'position' => rank, 'contestant' => []}
        slots.each do |slot|
          rankXML['contestant'] << {'name' => slot.contestant.name, 'location' => slot.contestant.location}
        end
        rankingXML['rank'] << rankXML
      end
      exp['ranking'] = rankingXML
    end
   
    require('xmlsimple') 
    XmlSimple.xml_out exp, {'RootName' => 'final', 'NoAttr' => false}    
  end

  def to_file(overwrite = true)
    archive_path = File.join(RAILS_ROOT, contest.game_definition.plugin_guid + "_finale.zip")
    xml_path = File.join(RAILS_ROOT, "final.xml")
    if File.exists? xml_path
      if overwrite
        File.delete xml_path
      else
        return nil
      end
    end
    file = File.open(xml_path, "w")
    file.write(self.export)
    file.close

    if File.exists? archive_path
      if overwrite
        File.delete archive_path
      else
        return nil
      end
    end

    replay_files = []
    rounds = self.matches.collect(&:rounds).flatten
    require 'zip/zip'
    Zip::ZipFile.open(archive_path, Zip::ZipFile::CREATE) {|zipfile|
      rounds.each do |round|
        file = File.join(RAILS_ROOT, "public", "system", "replays", round.id.to_s, "original", round.replay_file_name)
        zipfile.add(File.join("replays", round.replay_file_name), file)
      end
      zipfile.add File.basename(xml_path), xml_path
    }
    return archive_path
  end
end
