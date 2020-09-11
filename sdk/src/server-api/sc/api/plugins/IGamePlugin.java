package sc.api.plugins;

import sc.api.plugins.host.IGamePluginHost;
import sc.plugins.IPlugin;
import sc.shared.ScoreDefinition;

public interface IGamePlugin extends IPlugin<IGamePluginHost> {
  /** @return eine neues Spiel dieses Typs */
  IGameInstance createGame();

  ScoreDefinition getScoreDefinition();
}
