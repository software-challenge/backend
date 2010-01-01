module MatchesHelper
  def match_score_fragment_name(game, fragment)
    t("games.#{game.game_identifier.to_s.underscore}.match_score.#{fragment.name}")
  end

  def round_score_fragment_name(game, fragment)
    t("games.#{game.game_identifier.to_s.underscore}.round_score.#{fragment.name}")
  end

  def match_progress(match, separator = '/')
    [match.rounds.played.count, match.rounds.count].join(separator)
  end
end
