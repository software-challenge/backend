package sc.plugin_schaefchen;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.api.plugins.host.IGamePluginHost;
import sc.plugin_schaefchen.util.Configuration;
import sc.shared.ScoreAggregation;
import sc.shared.ScoreDefinition;
import sc.shared.ScoreFragment;
import edu.cau.plugins.PluginDescriptor;

/**
 * Minimal game plugin.
 * Basic structure of the game is described here
 * 
 * @author Sven Casimir, Torsten Krause
 * @since Juni, 2010
 */
@PluginDescriptor(name = "Schäfchen ins Trockene", uuid = GamePlugin.PLUGIN_UUID, author = GamePlugin.PLUGIN_AUTHOR)
public class GamePlugin implements IGamePlugin
{
	public static final String			PLUGIN_AUTHOR		= "Sven Casimir, Torsten Krause";
	public static final String			PLUGIN_UUID			= "minimal_plugin";

	public static final int				MAX_PLAYER_COUNT	= 2;

	public static final int				MAX_TURN_COUNT		= 50;

	public static final ScoreDefinition	SCORE_DEFINITION;

	static
	{
		SCORE_DEFINITION = new ScoreDefinition();
		SCORE_DEFINITION.add("Gewinner");
		SCORE_DEFINITION.add(new ScoreFragment("\u00D8 Feldnummer",
				ScoreAggregation.AVERAGE));
		SCORE_DEFINITION.add(new ScoreFragment("\u00D8 Karotten",
				ScoreAggregation.AVERAGE));
		SCORE_DEFINITION.add(new ScoreFragment("\u00D8 Zeit (ms)",
				ScoreAggregation.AVERAGE));
	}

	@Override
	public IGameInstance createGame()
	{
		Game g = new Game();
		// TODO fehlt evtl. Initialisierung?
		return g;
	}

	@Override
	public int getMaximumPlayerCount()
	{
		return MAX_PLAYER_COUNT;
	}

	@Override
	public void initialize(IGamePluginHost host)
	{
		host.registerProtocolClasses(Configuration.getClassesToRegister());
	}

	@Override
	public void unload()
	{
		// TODO Plugin entladen
	}

	@Override
	public ScoreDefinition getScoreDefinition()
	{
		return SCORE_DEFINITION;
	}

}
