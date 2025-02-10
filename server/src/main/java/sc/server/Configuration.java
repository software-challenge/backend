package sc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.shared.SharedConfiguration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

/**
 * Server configuration.
 *
 * TODO load values at startup from a properties file
 */
public class Configuration {
  private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

  public static final char DEBUG_SHORT_OPTION = 'd';
  public static final String DEBUG_OPTION = "debug";

  public static final String GAMELOADFILE = "loadGameFile";
  public static final String TURN_TO_LOAD = "turn";
  public static final String SAVE_REPLAY = "saveReplay";
  public static final String PAUSED = "paused";
  public static final String TIMEOUT = "timeout";
  public static final String LISTEN_LOCAL_KEY = "local";

  public static final String PASSWORD_KEY = "password";
  public static final String PORT_KEY = "port";
  public static final String PLUGINS_OPTION = "plugins";
  public static final String PLUGINS_PATH_DEFAULT = "./plugins";

  private static final Properties properties = new Properties();

  public static void loadServerProperties() {
    File file = new File("server.properties").getAbsoluteFile();
    if (file.exists()) {
      try {
        logger.info("Loading configuration from {}", file);
        Configuration.load(new FileReader(file));
        return;
      } catch (IOException e) {
        logger.error("Could not load server.properties, will use default values: {}", e.toString());
      }
    } else {
      logger.warn("Could not find server.properties at {}, will use default values!", file);
    }
    // Defaults, only if server.properties cannot be loaded
    properties.setProperty(PASSWORD_KEY, "examplepassword");
  }

  public static void load(Reader reader) throws IOException {
    properties.load(reader);
  }

  public static int getPort() {
    return get(PORT_KEY, Integer.class, SharedConfiguration.DEFAULT_PORT);
  }

  public static boolean getTimeout() {
    return get(TIMEOUT, Boolean.class, true);
  }

  public static boolean getListenLocal() {
    return get(LISTEN_LOCAL_KEY, Boolean.class, true);
  }

  public static String getPluginPath() {
    return get(PLUGINS_OPTION, String.class, PLUGINS_PATH_DEFAULT);
  }

  public static String getAdministrativePassword() {
    return get(PASSWORD_KEY);
  }

  /** Modifies a property within the configuration. */
  public static void set(final String key, final String value) {
    properties.setProperty(key, value);
  }

  public static void set(final String key, final boolean value) {
    properties.setProperty(key, String.valueOf(value));
  }

  public static void setIfNotNull(final String key, final String value) {
    if (value != null) {
      set(key, value);
    }
  }

  public static String get(final String key) {
    return get(key, String.class, null);
  }

  public static <T> T get(String key, Class<T> type, T defaultValue) {
    String stringValue = properties.getProperty(key);

    if (stringValue == null) {
      return defaultValue;
    }
    if (type == String.class) {
      return type.cast(stringValue);
    } else if (type == Integer.class) {
      return type.cast(Integer.parseInt(stringValue));
    } else if (type == Boolean.class) {
      return type.cast(toBoolean(stringValue));
    } else {
      throw new RuntimeException("Could not convert String to " + type.toString());
    }
  }

  private static boolean toBoolean(String value) {
    if ("true".equalsIgnoreCase(value)) {
      return true;
    } else if ("false".equalsIgnoreCase(value)) {
      return false;
    } else {
      throw new IllegalArgumentException("Argument '" + value + "' should be true or false");
    }
  }

}
