module MatchdaysHelper
  def matchday_status(matchday)
    if matchday.running?
      I18n.t("helpers.playing_in_progress")
    elsif matchday.played?
      I18n.t("helpers.played")
    else
      I18n.t("not_played_yet")
    end
  end
end
