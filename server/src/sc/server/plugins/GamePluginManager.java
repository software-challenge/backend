package sc.server.plugins;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.helpers.CollectionHelper;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;

public class GamePluginManager extends PluginManager<GamePluginInstance> {
  protected static Logger logger = LoggerFactory.getLogger(GamePluginInstance.class);

  public void activateAllPlugins() {
    for (GamePluginInstance plugins : getAvailablePlugins()) {
      try {
        plugins.load();
      } catch (PluginLoaderException e) {
        logger.error("Failed to load plugin.", e);
      }
    }
  }

  @Override
  public void reload() {
    GamePluginInstance i = new GamePluginInstance(sc.plugin2021.GamePlugin.class);
    this.availablePlugins.add(i);
  }

  @Override
  protected GamePluginInstance createPluginInstance(Class<?> definition, URI jarUri) {
    GamePluginInstance instance = new GamePluginInstance(definition);
    logger.info("GamePlugin '{}' {{}} was loaded.", instance
            .getDescription().name(), instance.getDescription().uuid());
    return instance;
  }

  public IGameInstance createGameOf(String gameType)
          throws UnknownGameTypeException {
    for (GamePluginInstance plugin : getAvailablePlugins()) {
      if (plugin.getDescription().uuid().equals(gameType)) {
        return plugin.getPlugin().createGame();
      }
    }

    throw new UnknownGameTypeException("Could not create a game of type: "
            + gameType, getPluginUUIDs());
  }

  public void loadPlugin(Class<?> type)
          throws PluginLoaderException {
    GamePluginInstance instance = new GamePluginInstance(type);
    instance.load();
    this.addPlugin(instance);
  }

  public Collection<String> supportedGames() {
    Collection<String> result = new HashSet<String>();
    for (GamePluginInstance plugin : getAvailablePlugins()) {
      result.add(plugin.getDescription().uuid());
    }
    return result;
  }

  public GamePluginInstance getPlugin(String uuid) {
    for (GamePluginInstance plugin : getAvailablePlugins()) {
      if (plugin.getDescription().uuid().equals(uuid)) {
        return plugin;
      }
    }
    return null;
  }

  /** Checks whether the plugin manager has a plugin of the specified UUID. */
  public boolean supportsGame(String uuid) {
    return getPlugin(uuid) != null;
  }

  public Iterable<String> getPluginUUIDs() {
    return CollectionHelper.map(this.getAvailablePlugins(), val -> val.getDescription().uuid());
  }

  @Override
  protected Class<?> getPluginInterface() {
    return IGamePlugin.class;
  }
}
