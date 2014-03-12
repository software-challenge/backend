package server.sc.plugin2015;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.api.plugins.host.IGamePluginHost;
import shared.sc.plugin2015.util.Configuration;
import sc.plugins.PluginDescriptor;
import sc.shared.ScoreAggregation;
import sc.shared.ScoreDefinition;
import sc.shared.ScoreFragment;

/**
 * Minimal game plugin. Basic structure of the game is described here
 * 
 * @author Sven Casimir, Torsten Krause
 * @since Juni, 2010
 */
@PluginDescriptor(name = GamePlugin.PLUGIN_NAME, uuid = GamePlugin.PLUGIN_UUID, author = GamePlugin.PLUGIN_AUTHOR)
public class GamePlugin implements IGamePlugin {
	public static final String PLUGIN_AUTHOR = "Torsten Krause, Sven Casimir";
	public static final String PLUGIN_NAME = "Manhattan";
	public static final String PLUGIN_UUID = "swc_2012_manhattan";
	public static final int PLUGIN_YEAR = 2012;

	public static final int MAX_PLAYER_COUNT = 2;
	public static final int MAX_TURN_COUNT = 24 + 4;

	public static final ScoreDefinition SCORE_DEFINITION;

	static {
		SCORE_DEFINITION = new ScoreDefinition();
		SCORE_DEFINITION.add(new ScoreFragment("Siegpunkte", ScoreAggregation.SUM, false));				
		SCORE_DEFINITION.add(new ScoreFragment("Punkte", ScoreAggregation.AVERAGE));
		SCORE_DEFINITION.add(new ScoreFragment("# Gebäude", ScoreAggregation.SUM, false));
		SCORE_DEFINITION.add(new ScoreFragment("# Städte", ScoreAggregation.SUM, false));
		SCORE_DEFINITION.add(new ScoreFragment("# höchste Türme", ScoreAggregation.SUM, false));
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
	public void initialize(IGamePluginHost host) {
		host.registerProtocolClasses(Configuration.getClassesToRegister());
	}

	@Override
	public void unload() {

	}

	@Override
	public ScoreDefinition getScoreDefinition() {
		return SCORE_DEFINITION;
	}

}
