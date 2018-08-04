package sc.plugin2019;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.api.plugins.host.IGamePluginHost;
import sc.plugin2019.util.Configuration;
import sc.plugins.PluginDescriptor;
import sc.shared.ScoreAggregation;
import sc.shared.ScoreDefinition;
import sc.shared.ScoreFragment;

/**
 * Abstrakte Beschreibung des Spieles Prianahs für die Software-Challenge 2019
 */
@PluginDescriptor(name = "Piranhas", uuid = GamePlugin.PLUGIN_UUID, author = GamePlugin.PLUGIN_AUTHOR)
public class GamePlugin implements IGamePlugin {

  public static final String PLUGIN_AUTHOR = "";
  public static final String PLUGIN_UUID = "swc_2019_piranhas";

  public static final ScoreDefinition SCORE_DEFINITION;

  static {
    SCORE_DEFINITION = new ScoreDefinition();
    SCORE_DEFINITION.add("Gewinner");
    // NOTE: Always write the XML representation of unicode characters, not the character directly, as it confuses the
    // parsers which consume the server messages!
    SCORE_DEFINITION.add(new ScoreFragment("\u2205 Schwarm", ScoreAggregation.AVERAGE));
  }

  @Override
  public IGameInstance createGame() {
    return new Game(PLUGIN_UUID);
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
