package sc.server.gaming;

import java.util.Collection;

import sc.api.plugins.IGamePluginHost;
import sc.server.Configuration;

public class GamePluginApi implements IGamePluginHost
{
	@Override
	public void registerProtocolClass(Class<?> clazz, String alias)
	{
		registerProtocolClass(clazz);
		Configuration.getXStream().alias(alias, clazz);
	}

	@Override
	public void registerProtocolClass(Class<?> clazz)
	{
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
