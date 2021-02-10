package sc.server.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/** Load & provide information about plugins. */
public class GamePluginManager extends PluginManager<IGamePlugin, GamePluginInstance> {
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

  /** Clear all available plugins and add all subclasses of {@link IGamePlugin} available through {@link ServiceLoader}. */
  @Override
  public void reload() {
    this.availablePlugins.clear();
    ServiceLoader.load(getPluginInterface()).iterator().forEachRemaining((plugin) -> {
      this.availablePlugins.add(new GamePluginInstance(plugin));
    });
  }

  @Override
  protected GamePluginInstance createPluginInstance(Class<? extends IGamePlugin> definition) {
    GamePluginInstance instance = new GamePluginInstance(definition);
    logger.info("GamePlugin '{}' {{}} was loaded.", instance
            .getDescription().name(), instance.getDescription().uuid());
    return instance;
  }

  public IGameInstance createGameOf(String gameType) throws UnknownGameTypeException {
    for (GamePluginInstance plugin : getAvailablePlugins()) {
      if (plugin.getDescription().uuid().equals(gameType)) {
        return plugin.getPlugin().createGame();
      }
    }

    throw new UnknownGameTypeException("Could not create a game of type: " + gameType, getPluginUUIDs());
  }

  public void loadPlugin(Class<? extends IGamePlugin> type) throws PluginLoaderException {
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
    return getAvailablePlugins().stream().map(val -> val.getDescription().uuid()).collect(Collectors.toList());
  }

  @Override
  protected Class<IGamePlugin> getPluginInterface() {
    return IGamePlugin.class;
  }
}
