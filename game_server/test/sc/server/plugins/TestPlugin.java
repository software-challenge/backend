package sc.server.plugins;


import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.api.plugins.host.IGamePluginHost;
import sc.plugins.PluginDescriptor;
import sc.shared.ScoreDefinition;

@PluginDescriptor(author="Chuck", name="TestPlugin", uuid=TestPlugin.TEST_PLUGIN_UUID)
public class TestPlugin implements IGamePlugin
{
	public static final String TEST_PLUGIN_UUID = "012345-norris";
	public static final int MAXIMUM_PLAYER_SIZE = 2;
	
	public static final ScoreDefinition	SCORE_DEFINITION;

	static
	{
		SCORE_DEFINITION = new ScoreDefinition();
		SCORE_DEFINITION.add("winner");
	}
	
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
		host.registerProtocolClass(TestTurnRequest.class);
		host.registerProtocolClass(TestGameState.class);
		host.registerProtocolClass(TestPlayer.class);
		host.registerProtocolClass(TestMove.class);
	}

	@Override
	public void unload()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public ScoreDefinition getScoreDefinition()
	{
		return SCORE_DEFINITION;
	}

}
