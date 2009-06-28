package sc.api.plugins;

import sc.api.IGamePluginHost;

public interface IGamePlugin extends IPlugin<IGamePluginHost>
{
	public IGameInstance createGame();

	public int getMaximumPlayerCount();
}
