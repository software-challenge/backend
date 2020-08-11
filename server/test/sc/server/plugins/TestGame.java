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
import sc.server.helpers.WinReason;
import sc.shared.*;

import java.util.List;
import java.util.Map;

public class TestGame extends RoundBasedGameInstance {
  private static final Logger logger = LoggerFactory.getLogger(TestGame.class);

  private TestGameState state = new TestGameState();

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
      next(this.state.getCurrentPlayer() == TestTeam.RED ? state.getRed() : state.getBlue(), false);
    }
  }

  @Override
  protected WinCondition checkWinCondition() {
    if (this.getRound() > 1) {
      logger.info("Someone won");
      return new WinCondition(((TestGameState) this.getCurrentState()).getState() % 2 == 0 ? TestTeam.RED : TestTeam.BLUE, WinReason.ROUND_LIMIT_FREE_FIELDS);
    }
    return null;
  }

  @Override
  public Player onPlayerJoined() throws TooManyPlayersException {
    List<Player> players = getPlayers();
    if (players.size() < 2) {
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

  public List<Player> getTestPlayers() {
    return getPlayers();
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
    result.put(player, new PlayerScore(false, "Spieler hat das Spiel verlassen."));
    result.get(player).setCause((cause != null) ? cause : ScoreCause.LEFT);
    notifyOnGameOver(result);
  }

  @Override
  public PlayerScore getScoreFor(Player p) {
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
    return null;
  }

  /** Sends welcomeMessage to all listeners and notify player on new gameStates or MoveRequests */
  @Override
  public void start() {
    for (final Player p : this.getPlayers()) {
      p.notifyListeners(new WelcomeMessage(p.getColor()));
    }

    super.start();
  }

  @Override
  protected ActionTimeout getTimeoutFor(Player player) {
    return new ActionTimeout(false, 100000000L, 20000000L);
  }

  @Override
  public String getPluginUUID() {
    return "test";
  }
}
