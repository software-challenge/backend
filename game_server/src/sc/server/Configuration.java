package sc.server;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.helpers.RuntimeJarLoader;
import sc.protocol.LobbyProtocol;
import sc.shared.SharedConfiguration;

import com.thoughtworks.xstream.XStream;

/**
 * Server configuration.
 *         TODO load values at startup from a properties file
 */
public class Configuration
{
  public static final String PASSWORD_KEY	= "password";
  public static final String PORT_KEY		= "port";
  public static final String PLUGIN_PATH_KEY	= "plugins";
  public static final char DEBUG_SHORT_OPTION = 'd';
  public static final String DEBUG_OPTION = "debug";
  public static final String PLUGINS_OPTION = "plugins";
  public static final String GAMELOADFILE_OPTION = "loadGameFile";
  public static final String GAMELOADFILE = "loadGameFile";
  public static final String TURN_OPTION = "turn";
  public static final String TURN_TO_LOAD = "turnToLoad";
  public static final String PLUGIN_PATH_DEFAULT_KEY = "./plugins";
  public static final String SAVE_REPLAY_OPTION = "saveReplay";
  public static final String SAVE_REPLAY = "saveReplay";
  public static final String TEST_MODE = "testMode";
  public static final int BIG_DECIMAL_SCALE = 6;
  public static final String PAUSED = "paused";
  public static final String TIMEOUT = "timeout";


  private static final Logger				logger			= LoggerFactory
          .getLogger(Configuration.class);
  private static final XStream			xStream;
  private static final RuntimeJarLoader	xStreamClassLoader;
  private static final Properties			properties		= new Properties();

  static
  {
    xStream = new XStream();
    xStream.setMode(XStream.NO_REFERENCES);
    xStreamClassLoader = AccessController
            .doPrivileged(new PrivilegedAction<RuntimeJarLoader>() {
              @Override
              public RuntimeJarLoader run()
              {
                return new RuntimeJarLoader(xStream.getClassLoader());
              }
            });
    xStream.setClassLoader(xStreamClassLoader);
//		xStream.registerConverter(new PerspectiveAwareConverter(xStream
//				.getMapper(), xStream.getReflectionProvider()));
    LobbyProtocol.registerMessages(xStream);
  }

  public static void load(Reader reader) throws IOException
  {
    properties.load(reader);
  }

  public static int getPort()
  {
    return get(PORT_KEY, Integer.class, SharedConfiguration.DEFAULT_PORT);
  }

  public static XStream getXStream()
  {
    return xStream;
  }

  public static void addXStreamClassloaderURL(URL url)
  {
    xStreamClassLoader.addURL(url);
  }

  public static String getPluginPath()
  {
    return get(PLUGIN_PATH_KEY, String.class, PLUGIN_PATH_DEFAULT_KEY);
  }

  public static String getAdministrativePassword()
  {
    return get(PASSWORD_KEY);
  }

  /**
   * Modifies a property within the configuration.
   *
   * @param key
   * @param value
   */
  public static void set(final String key, final String value)
  {
    properties.setProperty(key, value);
  }

  public static void setIfNotNull(final String key, final String value)
  {
    if (value != null)
    {
      set(key, value);
    }
  }

  public static String get(final String key)
  {
    return get(key, String.class, null);
  }

  public static <T> T get(String key, Class<T> type, T defaultValue)
  {
    String stringValue = properties.getProperty(key);

    if (stringValue == null)
    {
      return defaultValue;
    }

    try
    {
      if (type == String.class)
      {
        return type.cast(stringValue);
      }
      else if (type == Integer.class)
      {
        return type.cast(Integer.parseInt(stringValue));
      }
      else
      {
        logger.warn("Could not convert String to {} ", type);
        return defaultValue;
      }
    }
    catch (Exception e)
    {
      logger.warn("Failed to retrieve key from configuration.", e);
      return defaultValue;
    }
  }
}
