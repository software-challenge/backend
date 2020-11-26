package sc.api.plugins;

import sc.plugins.IPlugin;
import sc.shared.ScoreDefinition;

public interface IGamePlugin extends IPlugin {
  String id();

  /** @return eine neues Spiel dieses Typs. */
  IGameInstance createGame();

  ScoreDefinition getScoreDefinition();
}
