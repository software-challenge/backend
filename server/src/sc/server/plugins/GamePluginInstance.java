package sc.server.plugins;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;

/** Wrapper for a {@link IGamePlugin} instance. */
public class GamePluginInstance extends PluginInstance<IGamePlugin> {

  public GamePluginInstance(Class<? extends IGamePlugin> definition) {
    super(definition);
  }

  public GamePluginInstance(IGamePlugin instance) {
    super(instance);
  }

  public IGameInstance createGame() {
    return this.getPlugin().createGame();
  }

}
