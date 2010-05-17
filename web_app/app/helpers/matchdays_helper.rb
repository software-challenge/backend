	module MatchdaysHelper
	  def matchday_status(matchday)
	    if matchday.running? and ((not current_user.nil? and current_user.has_role?(:administrator)) or matchday.published?)
	      I18n.t("helpers.playing_in_progress")
	    elsif matchday.played? and ((not current_user.nil? and current_user.has_role?(:administrator)) or matchday.published?)
	      I18n.t("helpers.played") + (matchday.published? ? "" : " aber nicht veröffentlicht")
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
      all_contestants = matchday.contest.contestants.without_testers.visible.all
      played_contestants = returning(Array.new) do |contestants|
        matchday.matches.each do |match|
          match.slots.each do |slot|
            contestants << slot.contestant
          end
        end
      end
      not_played_contestants = all_contestants - played_contestants

      if not_played_contestants.size == 1
        return not_played_contestants.first
      else
        return nil
      end
    end
end
