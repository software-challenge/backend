package sc.server.plugins;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.api.plugins.IGamePluginHost;
import sc.api.plugins.PluginDescriptor;

@PluginDescriptor(author="Chuck", name="TestPlugin", uuid=TestPlugin.TEST_PLUGIN_UUID)
public class TestPlugin implements IGamePlugin
{
	public static final String TEST_PLUGIN_UUID = "012345-norris";
	
	public TestPlugin()
	{
		
	}
	
	@Override
	public IGameInstance createGame()
	{
		return new TestGame();
	}

	@Override
	public void initialize(IGamePluginHost host)
	{
		host.registerProtocolClass(TestPlayer.class);
		host.registerProtocolClass(TestMove.class);
	}

	@Override
	public void unload()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMaximumPlayerCount()
	{
		return 2;
	}

}
