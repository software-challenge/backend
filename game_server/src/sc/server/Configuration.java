package sc.server;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.protocol.LobbyProtocol;
import com.thoughtworks.xstream.XStream;

/**
 * Server configuration.
 * 
 * @author mja
 * @author rra
 * 
 *         TODO load values at startup from a properties file
 */
public class Configuration
{
	public static final String	PASSWORD_KEY	= "password";
	public static final String	PORT_KEY		= "port";
	public static final String	PLUGIN_PATH_KEY	= "plugins";

	private static Logger		logger			= LoggerFactory
														.getLogger(Configuration.class);
	private static XStream		xStream;
	private static Properties	properties		= new Properties();

	static
	{
		xStream = new XStream();
		LobbyProtocol.registerMessages(xStream);
	}

	public static void load(Reader reader) throws IOException
	{
		properties.load(reader);
	}

	public static int getPort()
	{
		return get(PORT_KEY, Integer.class, 3000);
	}

	public static XStream getXStream()
	{
		return xStream;
	}

	public static String getPluginPath()
	{
		return get(PLUGIN_PATH_KEY, String.class, "./plugins");
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
