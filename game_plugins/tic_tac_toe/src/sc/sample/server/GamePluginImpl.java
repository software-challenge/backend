package sc.sample.server;

import edu.cau.plugins.PluginDescriptor;
import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.api.plugins.host.IGamePluginHost;
import sc.sample.protocol.ProtocolDefinition;
import sc.shared.ScoreDefinition;

@PluginDescriptor(name = "Tic Tac Toe (Sample)", uuid = GamePluginImpl.PLUGIN_UUID)
public class GamePluginImpl implements IGamePlugin
{
	public static final String			PLUGIN_UUID			= "sc_sample_plugin";
	public static final int				MAXIMUM_PLAYER_SIZE	= 2;
	public static final ScoreDefinition	SCORE_DEFINITION;

	static
	{
		SCORE_DEFINITION = new ScoreDefinition();
		SCORE_DEFINITION.add("winner");
		SCORE_DEFINITION.add("moves");
	}

	@Override
	public IGameInstance createGame()
	{
		return new GameInstanceImpl();
	}

	@Override
	public int getMaximumPlayerCount()
	{
		return MAXIMUM_PLAYER_SIZE;
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

	@Override
	public ScoreDefinition getScoreDefinition()
	{
		return SCORE_DEFINITION;
	}
}
