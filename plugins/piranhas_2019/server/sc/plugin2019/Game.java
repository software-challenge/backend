package sc.plugin2019;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.IGameState;
import sc.api.plugins.exceptions.GameLogicException;
import sc.api.plugins.host.GameLoader;
import sc.framework.plugins.AbstractPlayer;
import sc.framework.plugins.ActionTimeout;
import sc.framework.plugins.RoundBasedGameInstance;
import sc.plugin2019.util.Configuration;
import sc.plugin2019.util.Constants;
import sc.protocol.responses.ProtocolMessage;
import sc.shared.*;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Minimal game. Basis for new plugins. This class holds the game logic.
 */
@XStreamAlias(value = "game")
public class Game extends RoundBasedGameInstance<Player> {
  private static final Logger logger =LoggerFactory.getLogger(Game.class);

  @XStreamOmitField
  private List<PlayerColor> availableColors = new LinkedList<>();

  private GameState gameState = new GameState();

  public Game() {
    this.availableColors.add(PlayerColor.RED);
    this.availableColors.add(PlayerColor.BLUE);
  }

  public Game(String pluginUUID) {
    this();
    this.pluginUUID = pluginUUID;
  }

  public GameState getGameState() {
    return this.gameState;
  }

  @Override
  protected IGameState getCurrentState() {
    return this.gameState; // return visible board for the players
  }

  /**
   * Someone did something, check out what it was (move maybe? Then check the
   * move)
   */
  @Override
  protected void onRoundBasedAction(AbstractPlayer fromPlayer, ProtocolMessage data) throws GameLogicException, InvalidGameStateException, InvalidMoveException {

    Player author = (Player) fromPlayer;

    /*
     * NOTE: Checking if right player sent move was already done by
     * {@link sc.framework.plugins.RoundBasedGameInstance#onAction(AbstractPlayer, ProtocolMove)}.
     * There is no need to do it here again.
     */
    try {
      if (!(data instanceof Move)) {
        throw new InvalidMoveException(author.getDisplayName() + " hat kein Zug-Objekt gesendet.");
      }

      final Move move = (Move) data;
      System.out.println("move perform");
      move.perform(this.gameState);
      next(this.gameState.getCurrentPlayer());
    } catch (InvalidMoveException e) {
      super.catchInvalidMove(e, author);
    }
  }

  /**
   * In this game, a new round begins when both players made one move. The order
   * in which the players make their move may change.
   */
  protected boolean increaseTurnIfNecessary(Player nextPlayer) {
    return getGameState().getTurn() % 2 == 0;
  }

  @Override
  public AbstractPlayer onPlayerJoined() {
    final Player player;
    // When starting a game from a imported state the players should not be
    // overwritten
    PlayerColor playerColor = this.availableColors.remove(0);
    if (PlayerColor.RED == playerColor && this.gameState.getPlayer(PlayerColor.RED) != null) {
      player = this.gameState.getPlayer(PlayerColor.RED);
    } else if (PlayerColor.BLUE == playerColor && this.gameState.getPlayer(PlayerColor.BLUE) != null) {
      player = this.gameState.getPlayer(PlayerColor.BLUE);
    } else {
      player = new Player(playerColor);
    }

    this.players.add(player);
    this.gameState.addPlayer(player);

    return player;
  }

  /**
   * Sends welcomeMessage to all listeners and notify player on new gameStates or MoveRequests
   */
  @Override
  public void start() {
    for (final Player p : this.players) {
      p.notifyListeners(new WelcomeMessage(p.getPlayerColor()));
    }

    super.start();
  }

  @Override
  public PlayerScore getScoreFor(Player player) {
    logger.debug("get score for player {}", player.getPlayerColor());
    logger.debug("player violated: {}", player.getViolated());
    int[] stats = this.gameState.getPlayerStats(player);
    int matchPoints = Constants.DRAW_SCORE;
    WinCondition winCondition = checkWinCondition();
    String reason = null;
    AbstractPlayer opponent = gameState.getOpponent(player);
    if (winCondition != null) {
      reason = winCondition.getReason();
      if (player.getPlayerColor().equals(winCondition.getWinner())) {
        matchPoints = Constants.WIN_SCORE;
      } else if (opponent.getPlayerColor().equals(winCondition.getWinner())) {
        matchPoints = Constants.LOSE_SCORE;
      } else {
        // draw
        matchPoints = Constants.DRAW_SCORE;
      }
    }
    // opponent has done something wrong
    if (opponent.hasViolated() && !player.hasViolated() || opponent.hasLeft() && !player.hasLeft()
            || opponent.hasSoftTimeout() || opponent.hasHardTimeout()) {
      matchPoints = 2;
    }
    ScoreCause cause;
    if (player.hasSoftTimeout()) { // Soft-Timeout
      cause = ScoreCause.SOFT_TIMEOUT;
      reason = "Der Spieler hat innerhalb von " + (this.getTimeoutFor(null).getSoftTimeout() / 1000) + " Sekunden nach Aufforderung keinen Zug gesendet";
      matchPoints = 0;
    } else if (player.hasHardTimeout()) { // Hard-Timeout
      cause = ScoreCause.HARD_TIMEOUT;
      reason = "Der Spieler hat innerhalb von " + (this.getTimeoutFor(null).getHardTimeout() / 1000) + " Sekunden nach Aufforderung keinen Zug gesendet";
      matchPoints = 0;
    } else if (player.hasViolated()) { // rule violation
      cause = ScoreCause.RULE_VIOLATION;
      reason = player.getViolationReason(); // message from InvalidMoveException
      matchPoints = 0;
    } else if (player.hasLeft()) { // player left
      cause = ScoreCause.LEFT;
      reason = "Der Spieler hat das Spiel verlassen";
      matchPoints = 0;
    } else { // regular score or opponent violated
      cause = ScoreCause.REGULAR;
    }
    return new PlayerScore(cause, reason, matchPoints, stats[Constants.GAME_STATS_SWARM_SIZE]);
  }

  @Override
  //TODO set canTimeout to true
  protected ActionTimeout getTimeoutFor(Player player) {
    return new ActionTimeout(false, 10000L, 2000L);
  }

  /**
   * Checks if a win condition in the current game state is met.
   * Checks round limit and end of round (and playerStats).
   * Checks if goal is reached
   *
   * @return WinCondition with winner and reason or null, if no win condition is
   * yet met.
   */
  public WinCondition checkWinCondition() {
    // TODO check whether this is right
    int[][] stats = this.gameState.getGameStats();
    if (this.gameState.getTurn() < 2 * Constants.ROUND_LIMIT) {
      // round limit not reached
      AbstractPlayer winningPlayer = getWinner();
      if (winningPlayer != null) {
        return new WinCondition(winningPlayer.getPlayerColor(), Constants.WINNING_MESSAGE);
      } else {
        return null;
      }
    } else {
      // round limit reached
      AbstractPlayer winningPlayer = getWinner();
      if (winningPlayer != null) {
        return new WinCondition(winningPlayer.getPlayerColor(), Constants.WINNING_MESSAGE);
      } else {
        PlayerColor winner;
        if (stats[Constants.GAME_STATS_RED_INDEX][Constants.GAME_STATS_SWARM_SIZE] > stats[Constants.GAME_STATS_BLUE_INDEX][Constants.GAME_STATS_SWARM_SIZE]) {
          winner = PlayerColor.RED;
        } else if (stats[Constants.GAME_STATS_RED_INDEX][Constants.GAME_STATS_SWARM_SIZE] < stats[Constants.GAME_STATS_BLUE_INDEX][Constants.GAME_STATS_SWARM_SIZE]) {
          winner = PlayerColor.BLUE;
        } else {
          winner = null;
        }
        return new WinCondition(winner, Constants.ROUND_LIMIT_MESSAGE);
      }
    }
  }

  private AbstractPlayer getWinner() {
    if (gameState.isSwarmConnected(gameState.getPlayer(PlayerColor.RED))) {
      System.out.println("Swarm is connected for red");
      if (gameState.isSwarmConnected(gameState.getPlayer(PlayerColor.BLUE))) {
        System.out.println("Swarm is connected for blue");
        if (gameState.getPointsForPlayer(PlayerColor.RED) > gameState.getPointsForPlayer(PlayerColor.BLUE)) {
          return gameState.getPlayer(PlayerColor.RED);
        } else if (gameState.getPointsForPlayer(PlayerColor.RED) < gameState.getPointsForPlayer(PlayerColor.BLUE)) {
          return gameState.getPlayer(PlayerColor.BLUE);
        } else {
          return null;
        }
      }
      return gameState.getPlayer(PlayerColor.RED);
    } else if (gameState.isSwarmConnected(gameState.getPlayer(PlayerColor.BLUE))) {
      System.out.println("Swarm is not connected for red");
      return gameState.getPlayer(PlayerColor.BLUE);
    }
    return null;
  }

  @Override
  public void loadFromFile(String file) {
    logger.info("Loading game from: " + file);
    GameLoader gl = new GameLoader(new Class<?>[]{GameState.class});
    Object gameInfo = gl.loadGame(Configuration.getXStream(), file);
    if (gameInfo != null) {
      loadGameInfo(gameInfo);
    }
  }

  @Override
  public void loadFromFile(String file, int turn) {
    logger.info("Loading game from: " + file + " at turn: " + turn);
    // only copy right gameState specified by turn
    try {
      FileReader fileReader = new FileReader(file);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      FileWriter fileWriter = new FileWriter("./tmp_replay.xml");
      BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
      String line;
      bufferedWriter.write("<protocol>"); // XXX since hui18 replays start with protocol instead of object-stream
      bufferedWriter.newLine();
      while ((line = bufferedReader.readLine()) != null) {
        if (line.contains("turn=\"" + turn + "\"")) {
          bufferedWriter.write(line);
          bufferedWriter.newLine();
          // case a gameState with specified turn was found
          while ((line = bufferedReader.readLine()) != null
                  && !line.contains("turn=\"" + (turn + 1) + "\"")) {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
          }
        }

      }
      bufferedWriter.write("</protocol>");
      bufferedWriter.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
    GameLoader gl = new GameLoader(new Class<?>[]{GameState.class});
    Object gameInfo = gl.loadGame(Configuration.getXStream(), "./tmp_replay.xml");
    if (gameInfo != null) {
      loadGameInfo(gameInfo);
    }
    // delete copied
    File tmp_replay = new File("./tmp_replay.xml");
    tmp_replay.delete();
  }

  // XXX test this
  @Override
  public void loadGameInfo(Object gameInfo) {
    logger.info("Processing game information");
    if (gameInfo instanceof GameState) {
      this.gameState = (GameState) gameInfo;
      // the currentPlayer has to be RED (else the Move request is send to the
      // wrong player)
      // if it isn't red, the players have to be switched and red is made
      // currentPlayer
      if (this.gameState.getCurrentPlayerColor() != PlayerColor.RED) {
        this.gameState.setCurrentPlayerColor(PlayerColor.RED);
        Player newRed = this.gameState.getPlayer(PlayerColor.BLUE).clone();
        newRed.setPlayerColor(PlayerColor.RED);
        Player newBlue = this.gameState.getPlayer(PlayerColor.RED).clone();
        newBlue.setPlayerColor(PlayerColor.BLUE);
        this.gameState.setRed(newRed);
        this.gameState.setBlue(newBlue);
      }
    }
  }

  @Override
  public List<AbstractPlayer> getWinners() {
    WinCondition win = checkWinCondition();
    List<AbstractPlayer> winners = new LinkedList<>();
    if (win != null) {
      for (Player player : this.players) {
        if (player.getPlayerColor() == win.getWinner()) {
          winners.add(player);
          break;
        }
      }
    } else {
      // No win condition met, player with highest score wins. Winning score is
      // determined by matchpoints ("Siegpunkte"). The winning player has 2
      // matchpoints. Find this player. If no player has 2 matchpoints, it is a
      // draw.
      for (Player player : this.players) {
        if (getScoreFor(player).getValues().get(0).intValueExact() == 2) {
          winners.add(player);
          break;
        }
      }
    }
    return winners;
  }

  /**
   * Returns all players. This should always be 2 the startplayer should be first in the List.
   *
   * @return List of all players
   */
  @Override
  public List<AbstractPlayer> getPlayers() {
    List<AbstractPlayer> players = new LinkedList<>();
    players.add(this.gameState.getPlayer(PlayerColor.RED));
    players.add(this.gameState.getPlayer(PlayerColor.BLUE));
    return players;
  }

  /**
   * Returns the PlayerScore for both players
   *
   * @return List of PlayerScores
   */
  @Override
  public List<PlayerScore> getPlayerScores() {
    LinkedList<PlayerScore> playerScores = new LinkedList<>();
    playerScores.add(getScoreFor(this.gameState.getPlayer(PlayerColor.RED)));
    playerScores.add(getScoreFor(this.gameState.getPlayer(PlayerColor.BLUE)));
    return playerScores;
  }

}
