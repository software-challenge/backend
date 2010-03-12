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
		if not round.played?.nil? 
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
              if (match.played? <= currentMatchday.played_at) and (not match.played?.nil?)
                count+=1 
              end
            end
	    return count
          end
end
