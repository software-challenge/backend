package sc.sample.server;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.api.plugins.IGamePluginHost;
import sc.api.plugins.PluginDescriptor;
import sc.sample.protocol.ProtocolDefinition;

@PluginDescriptor(name = "Tic Tac Toe (Sample)", uuid = GamePluginImpl.PLUGIN_UUID)
public class GamePluginImpl implements IGamePlugin
{
	public static final String PLUGIN_UUID = "sc_sample_plugin";
	@Override
	public IGameInstance createGame()
	{
		return new GameInstanceImpl();
	}

	@Override
	public int getMaximumPlayerCount()
	{
		return 2;
	}

	@Override
	public void initialize(IGamePluginHost host)
	{
		host.registerProtocolClasses(ProtocolDefinition.getProtocolClasses());
	}

	@Override
	public void unload()
	{
		// nothing to do

	}

}
