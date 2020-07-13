package sc.server.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.framework.plugins.Player;
import sc.shared.Team;

public class TestPlayer extends Player {
  private static Logger logger = LoggerFactory.getLogger(TestPlayer.class);

  public TestPlayer(Team pc) {
    super(pc);
  }

  public void requestMove() {
    TestTurnRequest request = new TestTurnRequest();
    logger.info(toString() + " is requesting a move");
    notifyListeners(request);
  }

}
