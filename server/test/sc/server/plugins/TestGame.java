package sc.server.plugins;

import org.slf4j.LoggerFactory;
import sc.api.plugins.IGameState;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.framework.plugins.Player;
import sc.framework.plugins.ActionTimeout;
import sc.framework.plugins.RoundBasedGameInstance;
import sc.protocol.responses.ProtocolMessage;
import sc.shared.*;

import java.util.List;
import java.util.Map;

public class TestGame extends RoundBasedGameInstance<TestPlayer> {
  private TestGameState state = new TestGameState();

  public TestGame() {
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
      next(this.state.getCurrentPlayer() == PlayerColor.RED ? state.getRed() : state.getBlue());
    }
  }

  @Override
  protected WinCondition checkWinCondition() {
    if (this.getRound() > 1) {
      System.out.println("Someone won");
      return new WinCondition(
              ((TestGameState) this.getCurrentState()).getState() % 2 == 0 ? PlayerColor.RED : PlayerColor.BLUE, "Round limit reached");
    }
    return null;
  }

  @Override
  public Player onPlayerJoined() throws TooManyPlayersException {
    if (this.players.size() < 2) {
      if (players.size() == 0) {
        state.setRed(new TestPlayer(PlayerColor.RED));
        players.add(state.getRed());
        return state.getRed();
      } else {
        state.setBlue(new TestPlayer(PlayerColor.BLUE));
        players.add(state.getBlue());
        return state.getBlue();
      }
    }

    throw new TooManyPlayersException();

  }

  public List<TestPlayer> getTestPlayers() {
    return this.players;
  }

  /**
   * Returns the PlayerScore for both players
   *
   * @return List of PlayerScores
   */
  @Override
  public List<PlayerScore> getPlayerScores() {
    return null;
  }

  @Override
  protected IGameState getCurrentState() {
    return this.state;
  }

  @Override
  public void onPlayerLeft(Player player, ScoreCause cause) {
    // this.players.remove(player);
    LoggerFactory.getLogger(this.getClass())
            .debug("Player left {}", player);
    Map<Player, PlayerScore> result = generateScoreMap();
    result.put(player, new PlayerScore(false, "Spieler hat das Spiel verlassen."));
    result.get(player).setCause(cause);
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
    // TODO Auto-generated method stub

  }

  @Override
  public void loadFromFile(String file, int turn) {
    // TODO Auto-generated method stub

  }

  @Override
  public void loadGameInfo(Object gameInfo) {
    // TODO Auto-generated method stub

  }

  @Override
  public List<Player> getWinners() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Returns all players. This should always be 2 and the startplayer should be first in the List.
   *
   * @return List of all players
   */
  @Override
  public List<Player> getPlayers() {
    return null;
  }

  /** Sends welcomeMessage to all listeners and notify player on new gameStates or MoveRequests */
  @Override
  public void start() {
    for (final TestPlayer p : this.players) {
      p.notifyListeners(new WelcomeMessage(p.color));
    }

    super.start();
  }

  // XXX set to right value
  @Override
  protected ActionTimeout getTimeoutFor(TestPlayer player) {
    return new ActionTimeout(false, 100000000L, 20000000L);
  }

}
