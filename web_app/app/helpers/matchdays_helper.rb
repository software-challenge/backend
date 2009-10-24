module MatchdaysHelper
  def matchday_status(matchday)
    if matchday.job
      'läuft gerade'
    elsif matchday.played?
      'gespielt'
    else
      'ausstehend'
    end
  end
end
