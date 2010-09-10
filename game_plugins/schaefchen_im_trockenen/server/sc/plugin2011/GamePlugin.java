package sc.plugin2011;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.api.plugins.host.IGamePluginHost;
import sc.plugin2011.util.Configuration;
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
	public static final String PLUGIN_NAME = "Schäfchen im Trockenen";
	public static final String PLUGIN_UUID = "swc_2011_schaefchen_im_trockenen";


	public static final int MAX_PLAYER_COUNT = 2;

	// TODO: warum ist hier einer mehr nötig?
	public static final int MAX_TURN_COUNT = 30;

	public static final ScoreDefinition SCORE_DEFINITION;

	static {
		SCORE_DEFINITION = new ScoreDefinition();
		SCORE_DEFINITION.add("Siegpunkte");
		SCORE_DEFINITION.add(new ScoreFragment("\u00D8 Schafe im Spiel",
				ScoreAggregation.AVERAGE, false));
		SCORE_DEFINITION.add(new ScoreFragment("\u00D8 Gefangen Schafe",
				ScoreAggregation.AVERAGE, false));
		SCORE_DEFINITION.add(new ScoreFragment("\u00D8 Gestohlen Schafe",
				ScoreAggregation.AVERAGE, false));
		SCORE_DEFINITION.add(new ScoreFragment("\u00D8 Gesammelte Blumen",
				ScoreAggregation.AVERAGE, false));
		SCORE_DEFINITION.add(new ScoreFragment("\u00D8 Gefressen Blumen",
				ScoreAggregation.AVERAGE, false));
		SCORE_DEFINITION.add(new ScoreFragment("\u00D8 Punkte",
				ScoreAggregation.AVERAGE));
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
		// TODO Plugin entladen
	}

	@Override
	public ScoreDefinition getScoreDefinition() {
		return SCORE_DEFINITION;
	}

}
