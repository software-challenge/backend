package sc.plugin2018;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.api.plugins.host.IGamePluginHost;
import sc.plugin2018.util.Configuration;
import sc.plugins.PluginDescriptor;
import sc.shared.ScoreAggregation;
import sc.shared.ScoreDefinition;
import sc.shared.ScoreFragment;

/**
 * Die Beschreibung des Hase- und Igel Core-Plugins für die Software-Challenge
 * 2010.
 * 
 */
@PluginDescriptor(name = "Hase und Igel", uuid = GamePlugin.PLUGIN_UUID, author = GamePlugin.PLUGIN_AUTHOR)
public class GamePlugin implements IGamePlugin
{
	public static final String			PLUGIN_AUTHOR		= "";
	public static final String			PLUGIN_UUID			= "swc_2018_hase_und_igel";

	public static final int				MAX_PLAYER_COUNT	= 2;

	public static final int				MAX_TURN_COUNT		= 30;

	public static final ScoreDefinition	SCORE_DEFINITION;

	static
	{
		SCORE_DEFINITION = new ScoreDefinition();
		SCORE_DEFINITION.add("Gewinner");
		SCORE_DEFINITION.add(new ScoreFragment("\u00D8 Feldnummer",
				ScoreAggregation.AVERAGE));
		SCORE_DEFINITION.add(new ScoreFragment("\u00D8 Karotten",
				ScoreAggregation.AVERAGE));
	}

	@Override
	public IGameInstance createGame()
	{
		return new Game();
	}
	
	@Override
	public void initialize(IGamePluginHost host) {
		host.registerProtocolClasses(Configuration.getClassesToRegister());
	}

	@Override
	public void unload() {
		// TODO Plugin entladen
	}

	@Override
	public ScoreDefinition getScoreDefinition()
	{
		return SCORE_DEFINITION;
	}

}
