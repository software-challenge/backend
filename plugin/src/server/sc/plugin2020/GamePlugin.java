package sc.plugin2020;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.helpers.XStreamKt;
import sc.plugin2020.util.Configuration;
import sc.plugins.PluginDescriptor;
import sc.protocol.helpers.LobbyProtocol;
import sc.shared.ScoreAggregation;
import sc.shared.ScoreDefinition;
import sc.shared.ScoreFragment;

/**
 * Abstrakte Beschreibung des Spiels Hive für die Software-Challenge 2020
 */
@PluginDescriptor(name = "Hive", uuid = GamePlugin.PLUGIN_UUID, author = GamePlugin.PLUGIN_AUTHOR)
public class GamePlugin implements IGamePlugin {

  public static final String PLUGIN_AUTHOR = "";
  public static final String PLUGIN_UUID = "swc_2020_hive";

  // NOTE: Always write the XML representation of unicode characters, not the character directly, as it confuses the
  // parsers which consume the server messages!
  public static final ScoreDefinition SCORE_DEFINITION = new ScoreDefinition(new ScoreFragment[]{
          new ScoreFragment("Gewinner"),
          new ScoreFragment("\u2205 freie Felder", ScoreAggregation.AVERAGE)});

  @Override
  public IGameInstance createGame() {
    return new Game(PLUGIN_UUID);
  }

  @Override
  public void initialize() {
    LobbyProtocol.registerAdditionalMessages(XStreamKt.getXStream(), Configuration.getClassesToRegister());
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
