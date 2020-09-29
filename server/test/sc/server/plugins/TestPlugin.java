package sc.server.plugins;


import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.helpers.XStreamKt;
import sc.plugins.PluginDescriptor;
import sc.protocol.helpers.LobbyProtocol;
import sc.shared.ScoreDefinition;

import java.util.Arrays;

@PluginDescriptor(author = "Chuck", name = "TestPlugin", uuid = TestPlugin.TEST_PLUGIN_UUID)
public class TestPlugin implements IGamePlugin {
  public static final String TEST_PLUGIN_UUID = "012345-norris";

  public static final ScoreDefinition SCORE_DEFINITION = new ScoreDefinition("winner");

  @Override
  public IGameInstance createGame() {
    return new TestGame();
  }

  @Override
  public void initialize() {
    LobbyProtocol.registerAdditionalMessages(XStreamKt.getXStream(), Arrays.asList(TestTurnRequest.class, TestGameState.class, TestPlayer.class, TestMove.class));
  }

  @Override
  public void unload() {
  }

  @Override
  public ScoreDefinition getScoreDefinition() {
    return SCORE_DEFINITION;
  }

}
