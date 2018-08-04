package sc.api.plugins;

import sc.api.plugins.host.IGamePluginHost;
import sc.plugins.IPlugin;
import sc.shared.ScoreDefinition;

public interface IGamePlugin extends IPlugin<IGamePluginHost> {
  /**
   * Creates a new game of this type.
   *
   * @return new IGameInstance
   */
  public IGameInstance createGame();

  public ScoreDefinition getScoreDefinition();
}
