package sc.server.plugins;

import sc.api.IGamePluginHost;
import sc.api.plugins.IGamePlugin;

public class GamePluginInstance extends PluginInstance<IGamePluginHost, IGamePlugin>
{
	public GamePluginInstance(Class<?> definition)
	{
		super(definition);
	}
}
