package sc.server.plugins;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;

public class GamePluginInstance extends PluginInstance<IGamePlugin> {

  public GamePluginInstance(Class<?> definition) {
    super(definition);
  }

  public IGameInstance createGame() {
    return this.getPlugin().createGame();
  }

}
