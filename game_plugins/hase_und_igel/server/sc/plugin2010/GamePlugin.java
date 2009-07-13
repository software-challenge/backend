package sc.plugin2010;

import edu.cau.plugins.PluginDescriptor;
import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.api.plugins.ScoreDefinition;
import sc.api.plugins.host.IGamePluginHost;

/**
 * Die Beschreibung des Hase- und Igel Core-Plugins für die Software-Challenge
 * 2010.
 * 
 * @author rra
 * @since Jul 4, 2009
 */
@PluginDescriptor(name = "Hase und Igel", uuid = GamePlugin.PLUGIN_UUID, author = GamePlugin.PLUGIN_AUTHOR, version = GamePlugin.PLUGIN_VERSION)
public class GamePlugin implements IGamePlugin
{
	public static final String			PLUGIN_VERSION		= "0.1";
	public static final String			PLUGIN_AUTHOR		= "Raphael Randschau <rra@informatik.uni-kiel.de>";
	public static final String			PLUGIN_UUID			= "swc_2010_hase_und_igel";

	public static final int				MAX_PLAYER_COUNT	= 2;

	public static final int				MAX_TURN_COUNT		= 30;

	public static final ScoreDefinition	SCORE_DEFINITION;

	static
	{
		SCORE_DEFINITION = new ScoreDefinition();
		SCORE_DEFINITION.add("winner");
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
		host.registerProtocolClass(Player.class);
		host.registerProtocolClass(Move.class);
		host.registerProtocolClass(GameState.class);
		host.registerProtocolClass(Board.class);
		
		// TODO evtl. fehlende Klassen registrieren
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
