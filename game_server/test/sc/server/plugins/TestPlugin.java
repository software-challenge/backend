package sc.server.plugins;

import sc.api.IGamePluginHost;
import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize(IGamePluginHost host)
	{
		// TODO Auto-generated method stub
		
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
