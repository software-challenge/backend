package sc.server.plugins;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.api.plugins.IGamePluginHost;

public class GamePluginInstance extends PluginInstance<IGamePluginHost, IGamePlugin>
{
	public GamePluginInstance(Class<?> definition)
	{
		super(definition);
	}

	public IGameInstance createGame()
	{
		return this.getPlugin().createGame();
	}
}
