package sc.server.gaming;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.host.IGamePluginHost;
import sc.server.Configuration;

public class GamePluginApi implements IGamePluginHost
{
	private static Logger	logger	= LoggerFactory
											.getLogger(GamePluginApi.class);

	@Override
	public void registerProtocolClass(Class<?> clazz, String alias)
	{
		registerProtocolClass(clazz);
		Configuration.getXStream().alias(alias, clazz);
	}

	@Override
	public void registerProtocolClass(Class<?> clazz)
	{
		logger.info("Processing XStream annotations for {}", clazz);
		Configuration.getXStream().processAnnotations(clazz);
	}

	@Override
	public void registerProtocolClasses(
			Collection<Class<? extends Object>> protocolClasses)
	{
		if (protocolClasses != null)
		{
			for (Class<? extends Object> clazz : protocolClasses)
			{
				registerProtocolClass(clazz);
			}
		}
	}
}
