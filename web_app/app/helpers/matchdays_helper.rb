module MatchdaysHelper
  def matchday_status(matchday)
    if matchday.running?
      'läuft gerade'
    elsif matchday.played?
      'gespielt'
    else
      'ausstehend'
    end
  end
end
