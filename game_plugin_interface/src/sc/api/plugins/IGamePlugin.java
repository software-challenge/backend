package sc.api.plugins;

import sc.api.IGamePluginHost;
import sc.api.plugins.protocol.IPacketFactory;

public interface IGamePlugin extends IPlugin<IGamePluginHost>
{
	public IGame createGame();

	public IPacketFactory getPacketFactory();
}
