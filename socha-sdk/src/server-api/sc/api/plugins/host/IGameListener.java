package sc.api.plugins.host;

import java.util.Map;

import sc.api.plugins.IGameState;
import sc.framework.plugins.AbstractPlayer;
import sc.networking.InvalidScoreDefinitionException;
import sc.shared.PlayerScore;

public interface IGameListener {
  void onGameOver(Map<AbstractPlayer, PlayerScore> results) throws InvalidScoreDefinitionException;

  void onStateChanged(IGameState data);

  void onPaused(AbstractPlayer nextPlayer);
}
