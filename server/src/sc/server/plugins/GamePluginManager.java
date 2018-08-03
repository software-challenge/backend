package sc.server.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.helpers.CollectionHelper;
import sc.helpers.Function;
import sc.server.Configuration;
import sc.server.gaming.GamePluginApi;

import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;

public class GamePluginManager extends PluginManager<GamePluginInstance> {
  protected static Logger logger = LoggerFactory.getLogger(GamePluginInstance.class);

  public void activateAllPlugins(GamePluginApi context) {
    for (GamePluginInstance plugins : getAvailablePlugins()) {
      try {
        plugins.load(context);
      } catch (PluginLoaderException e) {
        logger.error("Failed to load plugin.", e);
      }
    }
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

  public void loadPlugin(Class<?> type, GamePluginApi context)
          throws PluginLoaderException {
    GamePluginInstance instance = new GamePluginInstance(type);
    instance.load(context);
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

  @Override
  public String getPluginFolder() {
    return Configuration.getPluginPath();
  }

  public Iterable<String> getPluginUUIDs() {
    return CollectionHelper.map(this.getAvailablePlugins(),
            new Function<GamePluginInstance, String>() {
              @Override
              public String operate(GamePluginInstance val) {
                return val.getDescription().uuid();
              }
            });
  }

  @Override
  protected Class<?> getPluginInterface() {
    return IGamePlugin.class;
  }

  @Override
  protected void addJarToClassloader(URL url) {
    Configuration.addXStreamClassloaderURL(url);
  }

}
