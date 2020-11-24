package sc.server.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.IGameState;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.framework.plugins.ActionTimeout;
import sc.framework.plugins.Player;
import sc.framework.plugins.RoundBasedGameInstance;
import sc.protocol.responses.ProtocolMessage;
import sc.server.helpers.TestTeam;
import sc.shared.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestGame extends RoundBasedGameInstance<TestPlayer> {
  private static final Logger logger = LoggerFactory.getLogger(TestGame.class);

  private TestGameState state = new TestGameState();

  public TestGame() {
    super(TestPlugin.TEST_PLUGIN_UUID);
  }

  @Override
  protected void onRoundBasedAction(Player fromPlayer, ProtocolMessage data) {
    if (data instanceof TestMove) {

      /*
       * NOTE: Checking if right player sent move was already done by
       * {@link sc.framework.plugins.RoundBasedGameInstance#onAction(Player, Object)}.
       * There is no need to do it here again.
       */

      final TestMove move = (TestMove) data;
      move.perform(this.state);
      next(this.state.getCurrentPlayer() == TestTeam.RED ? state.getRed() : state.getBlue());
    }
  }

  @Override
  public WinCondition checkWinCondition() {
    if (this.getRound() > 1) {
      logger.info("Someone won");
      return new WinCondition(((TestGameState) this.getCurrentState()).getState() % 2 == 0 ? TestTeam.RED : TestTeam.BLUE, new IWinReason() {
        public String getMessage() {
          return "TestTeam %s hat gewonnen";
        }
      });
    }
    return null;
  }

  @Override
  public Player onPlayerJoined() throws TooManyPlayersException {
    if (this.players.size() < 2) {
      if (players.size() == 0) {
        state.setRed(new TestPlayer(TestTeam.RED));
        players.add(state.getRed());
        return state.getRed();
      } else {
        state.setBlue(new TestPlayer(TestTeam.BLUE));
        players.add(state.getBlue());
        return state.getBlue();
      }
    }

    throw new TooManyPlayersException();

  }

  public List<TestPlayer> getTestPlayers() {
    return this.players;
  }

  @Override
  public List<PlayerScore> getPlayerScores() {
    return null;
  }

  @Override
  protected IGameState getCurrentState() {
    return state;
  }

  @Override
  public void onPlayerLeft(Player player, ScoreCause cause) {
    // this.players.remove(player);
    logger.debug("Player left {}", player);
    Map<Player, PlayerScore> result = generateScoreMap();
    result.put(player, new PlayerScore(cause, "Spieler hat das Spiel verlassen.", 0));
    notifyOnGameOver(result);
  }

  @Override
  public void onPlayerLeft(Player player) {
    onPlayerLeft(player, ScoreCause.LEFT);
  }

  @Override
  public PlayerScore getScoreFor(TestPlayer p) {
    return new PlayerScore(true, "Spieler hat gewonnen.");
  }

  @Override
  public void loadFromFile(String file) {
  }

  @Override
  public void loadFromFile(String file, int turn) {
  }

  @Override
  public void loadGameInfo(Object gameInfo) {
  }

  @Override
  public List<Player> getWinners() {
    return null;
  }

  @Override
  public List<Player> getPlayers() {
    return new ArrayList<>(players);
  }

  /** Sends welcomeMessage to all listeners and notify player on new gameStates or MoveRequests */
  @Override
  public void start() {
    for (final TestPlayer p : this.players) {
      p.notifyListeners(new WelcomeMessage(p.getColor()));
    }

    super.start();
  }

  @Override
  protected ActionTimeout getTimeoutFor(TestPlayer player) {
    return new ActionTimeout(false, 100000000L, 20000000L);
  }

}
