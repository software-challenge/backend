	module MatchdaysHelper
	  def matchday_status(matchday)
	    if matchday.running?
	      I18n.t("helpers.playing_in_progress")
	    elsif matchday.played?
	      I18n.t("helpers.played")
	    else
	      I18n.t("helpers.not_played_yet")
	    end
	  end
	  
	  def matchesCount(matchday)
	     matchcount = 0
	     matchday.matches.each do |match|
	       matchcount += match.rounds.count
	     end
	     return matchcount
	  end
	  
	  def matchesDone(matchday)
	    done = 0
	    matchday.matches.each do |match|
	      match.rounds.each do |round|
          if round.played?
            done += 1
          end
	      end
	    end
	    return done
	  end
	  
	  def count_matches_played_by_contestant(contestant, currentMatchday)
	    all_matches = contestant.matches
      count = 0
      all_matches.each do |match|
        if match.played? and match.played_at <= currentMatchday.played_at
          count+=1
        end
      end
	    return count
    end

    def not_played_contestant_on(matchday)
      if matchday.contest.contestants.without_testers.visible.count % 2 != 0
        played_contestants = matchday.slots.collect{|slot| slot.contestant}
        return (matchday.contest.contestants.without_testers.visible.all - played_contestants).first
      else
        return nil
      end
    end
end
