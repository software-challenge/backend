package sc.server.plugins;


import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.api.plugins.host.IGamePluginHost;
import sc.plugins.PluginDescriptor;
import sc.shared.ScoreDefinition;

@PluginDescriptor(author = "Chuck", name = "TestPlugin", uuid = TestPlugin.TEST_PLUGIN_UUID)
public class TestPlugin implements IGamePlugin {
  public static final String TEST_PLUGIN_UUID = "012345-norris";

  public static final ScoreDefinition SCORE_DEFINITION = new ScoreDefinition("winner");

  @Override
  public IGameInstance createGame() {
    return new TestGame();
  }

  @Override
  public void initialize(IGamePluginHost host) {
    host.registerProtocolClass(TestTurnRequest.class);
    host.registerProtocolClass(TestGameState.class);
    host.registerProtocolClass(TestPlayer.class);
    host.registerProtocolClass(TestMove.class);
  }

  @Override
  public void unload() {
  }

  @Override
  public ScoreDefinition getScoreDefinition() {
    return SCORE_DEFINITION;
  }

}
