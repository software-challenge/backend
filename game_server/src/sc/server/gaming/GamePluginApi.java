package sc.server.gaming;


import sc.api.plugins.IGamePluginHost;
import sc.server.Configuration;

public class GamePluginApi implements IGamePluginHost
{
	@Override
	public void registerProtocolClass(Class<?> clazz, String alias)
	{
		Configuration.getXStream().alias(alias, clazz);
	}

	@Override
	public void registerProtocolClass(Class<?> clazz)
	{
		Configuration.getXStream().alias(clazz.getName().toLowerCase(), clazz);		
	}
}
