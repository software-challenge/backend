package sc.plugin2014;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.api.plugins.host.IGamePluginHost;
import sc.plugin2014.util.Configuration;
import sc.plugins.PluginDescriptor;
import sc.shared.*;

/**
 * Minimal game plugin. Basic structure of the game is described here
 * 
 */
@PluginDescriptor(name = GamePlugin.PLUGIN_NAME, uuid = GamePlugin.PLUGIN_UUID, author = GamePlugin.PLUGIN_AUTHOR)
public class GamePlugin implements IGamePlugin {
    public static final String          PLUGIN_AUTHOR    = "Florian Fittkau";
    public static final String          PLUGIN_NAME      = "Qwirkle";
    public static final String          PLUGIN_UUID      = "swc_2014_qwirkle";
    public static final int             PLUGIN_YEAR      = 2014;

    public static final int             MAX_PLAYER_COUNT = 2;
    public static final int             MAX_TURN_COUNT   = 24 + 4;

    public static final ScoreDefinition SCORE_DEFINITION;

    static {
        SCORE_DEFINITION = new ScoreDefinition();
        SCORE_DEFINITION.add(new ScoreFragment("Siegpunkte",
                ScoreAggregation.SUM, false));
        SCORE_DEFINITION.add(new ScoreFragment("Punkte",
                ScoreAggregation.AVERAGE));
        SCORE_DEFINITION.add(new ScoreFragment("# Steine",
                ScoreAggregation.SUM, false));
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
