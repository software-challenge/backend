package sc.plugin2010;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.api.plugins.host.IGamePluginHost;
import sc.plugin2010.util.Configuration;
import sc.shared.ScoreAggregation;
import sc.shared.ScoreDefinition;
import sc.shared.ScoreFragment;
import edu.cau.plugins.PluginDescriptor;

/**
 * Die Beschreibung des Hase- und Igel Core-Plugins für die Software-Challenge
 * 2010.
 * 
 * @author rra
 * @since Jul 4, 2009
 */
@PluginDescriptor(name = "Hase und Igel", uuid = GamePlugin.PLUGIN_UUID, author = GamePlugin.PLUGIN_AUTHOR)
public class GamePlugin implements IGamePlugin
{
	public static final String			PLUGIN_AUTHOR		= "Raphael Randschau <rra@informatik.uni-kiel.de>";
	public static final String			PLUGIN_UUID			= "swc_2010_hase_und_igel";

	public static final int				MAX_PLAYER_COUNT	= 2;

	public static final int				MAX_TURN_COUNT		= 30;

	public static final ScoreDefinition	SCORE_DEFINITION;

	static
	{
		SCORE_DEFINITION = new ScoreDefinition();
		SCORE_DEFINITION.add("Gewinner");
		SCORE_DEFINITION.add(new ScoreFragment("\u00D8 Position", ScoreAggregation.AVERAGE));
		SCORE_DEFINITION.add(new ScoreFragment("\u00D8 Karotten", ScoreAggregation.AVERAGE));
		SCORE_DEFINITION.add(new ScoreFragment("\u00D8 Züge", ScoreAggregation.AVERAGE));
		SCORE_DEFINITION.add(new ScoreFragment("\u00D8 Zeit", ScoreAggregation.AVERAGE));
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
