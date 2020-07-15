package sc.server.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.framework.plugins.Player;
import sc.api.plugins.ITeam;

public class TestPlayer extends Player {
  private static Logger logger = LoggerFactory.getLogger(TestPlayer.class);

  public TestPlayer(ITeam<?> pc) {
    super(pc);
  }

  public void requestMove() {
    TestTurnRequest request = new TestTurnRequest();
    logger.info(toString() + " is requesting a move");
    notifyListeners(request);
  }

}
