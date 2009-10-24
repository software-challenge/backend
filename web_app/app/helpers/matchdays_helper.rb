module MatchdaysHelper
  def matchday_status(matchday)
    if matchday.job
      'aktiv'
    elsif matchday.played?
      'ja'
    else
      'nein'
    end
  end
end
