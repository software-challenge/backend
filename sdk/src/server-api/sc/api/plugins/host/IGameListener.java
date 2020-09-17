package sc.api.plugins.host;

import java.util.Map;

import sc.api.plugins.IGameState;
import sc.framework.plugins.Player;
import sc.networking.InvalidScoreDefinitionException;
import sc.shared.PlayerScore;

public interface IGameListener {
  void onGameOver(Map<Player, PlayerScore> results) throws InvalidScoreDefinitionException;

  void onStateChanged(IGameState data, boolean observersOnly);

  void onPaused(Player nextPlayer);
}
