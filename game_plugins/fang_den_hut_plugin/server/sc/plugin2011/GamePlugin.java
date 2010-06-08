package sc.plugin2011;

import edu.cau.plugins.PluginDescriptor;
import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.api.plugins.host.IGamePluginHost;
import sc.shared.ScoreAggregation;
import sc.shared.ScoreDefinition;
import sc.shared.ScoreFragment;

@PluginDescriptor(name = "Fang den Hut", uuid = GamePlugin.PLUGIN_UUID, author = GamePlugin.PLUGIN_AUTHOR)
public class GamePlugin implements IGamePlugin {
	
	public static final String			PLUGIN_AUTHOR		= "Sven Casimir, Torsten Krause";
	public static final String			PLUGIN_UUID			= "swc_2011_fang_den_hut";

	public static final int				MAX_PLAYER_COUNT	= 2;

	public static final int				MAX_TURN_COUNT		= 30;

	public static final ScoreDefinition	SCORE_DEFINITION;
	
	static
	{
		SCORE_DEFINITION = new ScoreDefinition();
		SCORE_DEFINITION.add("Gewinner");
		SCORE_DEFINITION.add(new ScoreFragment("\u00D8 Punkte",
				ScoreAggregation.SUM));
		SCORE_DEFINITION.add(new ScoreFragment("\u00D8 Zeit (ms)",
				ScoreAggregation.AVERAGE));
	}

	@Override
	public IGameInstance createGame() {
		// TODO Auto-generated method stub
		return new Game();
	}

	@Override
	public int getMaximumPlayerCount() {
		return 2;
	}

	@Override
	public ScoreDefinition getScoreDefinition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize(IGamePluginHost host) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub
		
	}

}
