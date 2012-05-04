package sc.plugin2013;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.api.plugins.host.IGamePluginHost;
import sc.plugin2013.util.Configuration;
import sc.plugins.PluginDescriptor;
import sc.shared.ScoreDefinition;

/**
 * Minimal game plugin. Basic structure of the game is described here
 * 
 * @author Felix Dubrownik, Niklas Paulsen
 * 
 */
@PluginDescriptor(name = GamePlugin.PLUGIN_NAME, uuid = GamePlugin.PLUGIN_UUID, author = GamePlugin.PLUGIN_AUTHOR)
public class GamePlugin implements IGamePlugin {
	public static final String PLUGIN_AUTHOR = "Felix Dubrownik, Niklas Paulsen";
	public static final String PLUGIN_NAME = "Cartagena";
	public static final String PLUGIN_UUID = "swc_2013_cartagena";
	public static final int PLUGIN_YEAR = 2013;

	public static final int MAX_PLAYER_COUNT = 2;
	public static final int MAX_TURN_COUNT = 30; //TODO sinvollen Wert finden
	
	public static final ScoreDefinition SCORE_DEFINITION;
	
	static{
		SCORE_DEFINITION = new ScoreDefinition();
		//TODO Fragmente festlegen
	}



	@Override
	public void initialize(IGamePluginHost host) {
		host.registerProtocolClasses(Configuration.getClassesToRegister());

	}

	@Override
	public void unload() {
		// TODO Plugin entladen (nicht benötigt??)

	}

	@Override
	public IGameInstance createGame() {
		return new Game();
	}

	@Override
	public int getMaximumPlayerCount() {
		return MAX_PLAYER_COUNT;
	}

	@Override
	public ScoreDefinition getScoreDefinition() {
		return SCORE_DEFINITION;
	}

}
