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
end
