package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.exceptions.GameLogicException;
import sc.api.plugins.host.GameLoader;
import sc.framework.plugins.ActionTimeout;
import sc.framework.plugins.RoundBasedGameInstance;
import sc.framework.plugins.SimplePlayer;
import sc.plugin2018.util.Configuration;
import sc.plugin2018.util.Constants;
import sc.protocol.responses.ProtocolMessage;
import sc.shared.*;

import java.io.*;
import java.util.*;

/** Minimal game. Basis for new plugins. This class holds the game logic. */
@XStreamAlias(value = "game")
public class Game extends RoundBasedGameInstance<Player> {
  private static final Logger logger = LoggerFactory.getLogger(Game.class);
  
  @XStreamOmitField
  private Deque<PlayerColor> availableColors = new ArrayDeque<>();
  
  private GameState gameState = new GameState();
  
  public Game() {
    availableColors.add(PlayerColor.RED);
    availableColors.add(PlayerColor.BLUE);
  }
  
  public Game(String pluginUUID) {
    this();
    this.pluginUUID = pluginUUID;
  }
  
  public GameState getGameState() {
    return this.gameState;
  }
  
  @Override
  /** @return board visible for the players */
  protected Object getCurrentState() {
    return this.gameState;
  }
  
  /** Someone did something, check out what it was (move maybe? Then check the move) */
  @Override
  protected void onRoundBasedAction(SimplePlayer fromPlayer, ProtocolMessage data) throws GameLogicException, InvalidGameStateException, InvalidMoveException {
    
    Player author = (Player) fromPlayer;
    
    /**
     * NOTE: Checking if right player sent move was already done by
     * {@link sc.framework.plugins.RoundBasedGameInstance#onAction(SimplePlayer, ProtocolMove)}.
     * There is no need to do it here again.
     */
    try {
      if (!(data instanceof Move))
        throw new InvalidMoveException(author.getDisplayName() + " hat kein Zug-Objekt gesendet.");
      
      final Move move = (Move) data;
      move.perform(this.gameState);
      next(this.gameState.getCurrentPlayer());
    } catch (InvalidMoveException e) {
      super.catchInvalidMove(e, author);
    }
  }
  
  @Override
  public SimplePlayer onPlayerJoined() {
    PlayerColor playerColor = this.availableColors.remove();
    // When starting a game from a imported state the players should not be overwritten
    Player player = gameState.getPlayer(playerColor);
    if (player == null)
      player = new Player(playerColor);
    
    this.players.add(player);
    this.gameState.addPlayer(player);
    
    return player;
  }
  
  /** Sends welcomeMessage to all listeners and notify player on new gameStates or MoveRequests */
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
    logger.debug("player violated: {}", player.hasViolated());
    int[] stats = gameState.getPlayerStats(player);
    
    PlayerColor playerColor = player.getPlayerColor();
    Player opponent = gameState.getPlayer(playerColor.opponent());
    int matchPoints = Constants.DRAW_SCORE;
    WinCondition winCondition = checkWinCondition();
    String reason = null;
    if (winCondition != null) {
      reason = winCondition.getReason();
      if (winCondition.getWinner() == playerColor) {
        matchPoints = Constants.WIN_SCORE;
      } else if (winCondition.getWinner() == playerColor.opponent()) {
        matchPoints = Constants.LOSE_SCORE;
      } else {
        // this should not happen
        logger.warn("winner has no known PlayerColor");
      }
    }
    if (opponent.hasViolated() && !player.hasViolated() || opponent.hasLeft() && !player.hasLeft()
        || opponent.hasSoftTimeout() || opponent.hasHardTimeout()) {
      // opponent has done something wrong
      matchPoints = Constants.WIN_SCORE;
    }
    ScoreCause cause;
    if (player.hasSoftTimeout()) { // Soft-Timeout
      cause = ScoreCause.SOFT_TIMEOUT;
      reason = "Der Spieler hat innerhalb von " + (this.getTimeoutFor(null).getSoftTimeout() / 1000) + " Sekunden nach Aufforderung keinen Zug gesendet";
      matchPoints = Constants.LOSE_SCORE;
    } else if (player.hasHardTimeout()) { // Hard-Timeout
      cause = ScoreCause.HARD_TIMEOUT;
      reason = "Der Spieler hat innerhalb von " + (this.getTimeoutFor(null).getHardTimeout() / 1000) + " Sekunden nach Aufforderung keinen Zug gesendet";
      matchPoints = Constants.LOSE_SCORE;
    } else if (player.hasViolated()) { // rule violation
      cause = ScoreCause.RULE_VIOLATION;
      reason = player.getViolationReason(); // message from InvalidMoveException
      matchPoints = Constants.LOSE_SCORE;
    } else if (player.hasLeft()) { // player left
      cause = ScoreCause.LEFT;
      reason = "Der Spieler hat das Spiel verlassen";
      matchPoints = Constants.LOSE_SCORE;
    } else { // regular score or opponent violated
      cause = ScoreCause.REGULAR;
    }
    return new PlayerScore(cause, reason, matchPoints, stats[Constants.GAME_STATS_FIELD_INDEX], stats[Constants.GAME_STATS_CARROTS]);
  }
  
  @Override
  protected ActionTimeout getTimeoutFor(Player player) {
    return new ActionTimeout(true, 10000L, 2000L);
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
    int[][] stats = this.gameState.getGameStats();
    if (this.gameState.getTurn() < 2 * Constants.ROUND_LIMIT) {
      // round limit not reached
      Player winningPlayer = checkGoalReached();
      if (winningPlayer != null) {
        return new WinCondition(winningPlayer.getPlayerColor(), Constants.IN_GOAL_MESSAGE);
      } else {
        return null;
      }
    } else {
      // round limit reached
      Player winningPlayer = checkGoalReached();
      if (winningPlayer != null) {
        return new WinCondition(winningPlayer.getPlayerColor(), Constants.IN_GOAL_MESSAGE);
      } else {
        PlayerColor winner;
        if (stats[Constants.GAME_STATS_RED_INDEX][Constants.GAME_STATS_FIELD_INDEX] > stats[Constants.GAME_STATS_BLUE_INDEX][Constants.GAME_STATS_FIELD_INDEX]) {
          winner = PlayerColor.RED;
        } else if (stats[Constants.GAME_STATS_RED_INDEX][Constants.GAME_STATS_FIELD_INDEX] < stats[Constants.GAME_STATS_BLUE_INDEX][Constants.GAME_STATS_FIELD_INDEX]) {
          winner = PlayerColor.BLUE;
        } else {
          if (stats[Constants.GAME_STATS_RED_INDEX][Constants.GAME_STATS_CARROTS] > stats[Constants.GAME_STATS_BLUE_INDEX][Constants.GAME_STATS_CARROTS]) {
            winner = PlayerColor.BLUE;
          } else {
            // red wins on draw, because red first entered the goal
            winner = PlayerColor.RED;
          }
        }
        return new WinCondition(winner, Constants.ROUND_LIMIT_MESSAGE);
      }
    }
  }
  
  /**
   * Checks if one player reached the goal (at the end of a round). If both player are in goal, the one with lesser carrots
   * wins, if they both have the same amount, red wins for first entering goal.
   *
   * @return the player who reached the goal or null if no player reached the
   * goal, only returns a player on the end of a round else always null
   */
  public Player checkGoalReached() {
    if (this.gameState.getTurn() % 2 == 0) { // even turn is right here, because method is called after perform
      // Checking field index is enough, if other conditions don't apply goal is not reachable
      Player red = this.gameState.getRedPlayer();
      Player blue = this.gameState.getBluePlayer();
      if (red.inGoal()) {
        if (blue.inGoal() && blue.getCarrots() < red.getCarrots()) {
          return blue;
        }
        return red;
      } else if (blue.inGoal()) {
        return blue;
      }
    }
    return null;
  }
  
  @Override
  public void loadFromFile(String file) {
    logger.info("Loading game from: " + file);
    loadReplay(file);
  }
  
  @Override
  public void loadFromFile(String file, int turn) {
    logger.info("Loading game from: " + file + " at turn: " + turn);
    File tmp_replay = new File("./tmp_replay.xml");
    // only copy right gameState specified by turn
    try {
      FileReader fileReader = new FileReader(file);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      FileWriter fileWriter = new FileWriter(tmp_replay);
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
    loadReplay("./tmp_replay.xml");
    tmp_replay.delete();
  }
  
  public void loadReplay(String replayFile) {
    Object info = new GameLoader(GameState.class).loadGame(Configuration.getXStream(), replayFile);
    if (info != null)
      loadGameInfo(info);
  }
  
  @Override
  public void loadGameInfo(Object gameInfo) {
    logger.info("Processing game information");
    if (gameInfo instanceof GameState) {
      this.gameState = (GameState) gameInfo;
      // when loading from a state the listeners are not initialized
      for (PlayerColor color : PlayerColor.values())
        gameState.getPlayer(color).initListeners();
      // the currentPlayer has to be RED (else the Move request is send to the wrong player)
      // if it isn't RED, the players have to be switched and RED is made currentPlayer
      if (this.gameState.getCurrentPlayerColor() != PlayerColor.RED) {
        this.gameState.setCurrentPlayer(PlayerColor.RED);
        for (Player player : gameState.getPlayers()) {
          PlayerColor newColor = player.getPlayerColor().opponent();
          player.setPlayerColor(newColor);
          gameState.setPlayer(newColor, player);
        }
      }
    }
  }
  
  @Override
  public List<SimplePlayer> getWinners() {
    WinCondition win = checkWinCondition();
    List<SimplePlayer> winners = new ArrayList<>();
    if (win != null) {
      for (Player player : this.players) {
        if (player.getPlayerColor() == win.getWinner()) {
          winners.add(player);
          break;
        }
      }
    } else {
      // No win condition met, player with highest score wins. Winning score is
      // determined by matchpoints ("Siegpunkte"). The winning player has 2 matchpoints.
      // Find this player. If no player has 2 matchpoints, it is a draw.
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
   * Returns all players. This should always be 2.
   * The startPlayer should be first in the List.
   *
   * @return List of all players
   */
  @Override
  public List<SimplePlayer> getPlayers() {
    return Arrays.asList(gameState.getPlayers());
  }
  
  /**
   * Returns the PlayerScore for both players
   *
   * @return List of PlayerScores
   */
  @Override
  public List<PlayerScore> getPlayerScores() {
    List<PlayerScore> playerScores = new ArrayList<>();
    for (Player player : gameState.getPlayers())
      playerScores.add(getScoreFor(player));
    return playerScores;
  }
  
}
