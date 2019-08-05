package sc.plugin2020;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.IGameState;
import sc.api.plugins.host.GameLoader;
import sc.framework.plugins.ActionTimeout;
import sc.framework.plugins.Player;
import sc.framework.plugins.RoundBasedGameInstance;
import sc.plugin2020.util.Configuration;
import sc.plugin2020.util.Constants;
import sc.plugin2020.util.GameRuleLogic;
import sc.protocol.responses.ProtocolMessage;
import sc.shared.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/** Minimales Spiel als Basis für neue Plugins. Diese Klasse enthält die Spielelogik. */
@XStreamAlias(value = "game")
public class Game extends RoundBasedGameInstance<Player> {
  private static final Logger logger = LoggerFactory.getLogger(Game.class);

  @XStreamOmitField
  private List<PlayerColor> availableColors = new ArrayList<>();

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
    return this.gameState;
  }

  /** Jemand hat etwas gesendet -> testen was es war (wenn es ein Zug war, dann validieren) */
  @Override
  protected void onRoundBasedAction(Player fromPlayer, ProtocolMessage data) throws InvalidGameStateException, InvalidMoveException {
    // NOTE: Checking if right player sent move was already done by onAction(Player, ProtocolMove)}.
    // There is no need to do it here again.
    try {
      if(!(data instanceof Move))
        throw new InvalidMoveException(fromPlayer.getDisplayName() + " hat kein Zug-Objekt gesendet.");
      final Move move = (Move) data;
      logger.debug("Performing DrawMove");
      this.gameState = GameRuleLogic.performMove(this.gameState, move);
      next(this.gameState.getCurrentPlayer());
    } catch(InvalidMoveException e) {
      super.catchInvalidMove(e, fromPlayer);
    }
  }

  @Override
  public Player onPlayerJoined() {
    final Player player;
    // When starting a game from a imported state the players should not be overwritten
    PlayerColor playerColor = this.availableColors.remove(0);
    if(PlayerColor.RED == playerColor && this.gameState.getPlayer(PlayerColor.RED) != null) {
      player = this.gameState.getPlayer(PlayerColor.RED);
    } else if(PlayerColor.BLUE == playerColor && this.gameState.getPlayer(PlayerColor.BLUE) != null) {
      player = this.gameState.getPlayer(PlayerColor.BLUE);
    } else {
      player = new Player(playerColor);
    }

    this.players.add(player);
    this.gameState.addPlayer(player);

    return player;
  }

  /** Sends welcomeMessage to all listeners and notify player on new gameStates or MoveRequests */
  @Override
  public void start() {
    for(final Player p : this.players) {
      p.notifyListeners(new WelcomeMessage(p.getColor()));
    }
    super.start();
  }

  @Override
  public PlayerScore getScoreFor(Player player) {
    logger.debug("get score for player {} (violated: {})", player.getColor(), player.hasViolated());
    int[] stats = this.gameState.getPlayerStats(player);
    int matchPoints = Constants.DRAW_SCORE;
    WinCondition winCondition = checkWinCondition();
    String reason = null;
    Player opponent = gameState.getOpponent(player);
    if(winCondition != null) {
      reason = winCondition.toString(gameState.getPlayer(winCondition.getWinner()).getDisplayName());
      if(player.getColor().equals(winCondition.getWinner())) {
        matchPoints = Constants.WIN_SCORE;
      } else if(opponent.getColor().equals(winCondition.getWinner())) {
        matchPoints = Constants.LOSE_SCORE;
      } else {
        // draw
        matchPoints = Constants.DRAW_SCORE;
      }
    } else {
      if(gameState.getTurn() == 2 * Constants.ROUND_LIMIT) {
        reason = WinReason.ROUND_LIMIT.getMessage(null);
        matchPoints = Constants.DRAW_SCORE;
      }
    }
    // opponent has done something wrong
    if(opponent.hasViolated() && !player.hasViolated() || opponent.hasLeft() && !player.hasLeft() || opponent.hasSoftTimeout() || opponent.hasHardTimeout()) {
      matchPoints = Constants.WIN_SCORE;
    }
    ScoreCause cause;
    if(player.hasSoftTimeout()) {
      cause = ScoreCause.SOFT_TIMEOUT;
      reason = "Der Spieler hat innerhalb von " + (this.getTimeoutFor(null).getSoftTimeout() / 1000) + " Sekunden nach Aufforderung keinen Zug gesendet";
      matchPoints = Constants.LOSE_SCORE;
    } else if(player.hasHardTimeout()) {
      cause = ScoreCause.HARD_TIMEOUT;
      reason = "Der Spieler hat innerhalb von " + (this.getTimeoutFor(null).getHardTimeout() / 1000) + " Sekunden nach Aufforderung keinen Zug gesendet";
      matchPoints = Constants.LOSE_SCORE;
    } else if(player.hasViolated()) {
      cause = ScoreCause.RULE_VIOLATION;
      // message from InvalidMoveException
      reason = player.getViolationReason();
      matchPoints = Constants.LOSE_SCORE;
    } else if(player.hasLeft()) {
      cause = ScoreCause.LEFT;
      reason = "Der Spieler hat das Spiel verlassen";
      matchPoints = Constants.LOSE_SCORE;
    } else { // regular score or opponent violated
      cause = ScoreCause.REGULAR;
    }
    return new PlayerScore(cause, reason, matchPoints, stats[Constants.GAME_STATS_ROUNDS]);
  }

  @Override
  protected ActionTimeout getTimeoutFor(Player player) {
    return new ActionTimeout(true, 10000L, 2000L);
  }

  /**
   * Checks if a win condition in the current game state is met.
   * Checks round limit and end of round (and playerStats).
   * Checks if goal is reached.
   *
   * @return WinCondition with winner and reason or null if no win condition is met yet.
   */
  public WinCondition checkWinCondition() {
    int[][] stats = this.gameState.getGameStats();
    if(this.gameState.getTurn() % 2 == 1) {
      return null;
    }

    boolean redConnected = GameRuleLogic.isQueenBlocked(gameState.getBoard(), PlayerColor.RED);
    boolean blueConnected = GameRuleLogic.isQueenBlocked(gameState.getBoard(), PlayerColor.BLUE);
    if(redConnected) {
      logger.info("Swarm is connected for red");
      if(blueConnected) {
        logger.info("Swarm is connected for blue");
        if(gameState.getPointsForPlayer(PlayerColor.RED) > gameState.getPointsForPlayer(PlayerColor.BLUE)) {
          return new WinCondition(PlayerColor.RED, WinReason.SWARM_LARGER);
        } else if(gameState.getPointsForPlayer(PlayerColor.RED) < gameState.getPointsForPlayer(PlayerColor.BLUE)) {
          return new WinCondition(PlayerColor.BLUE, WinReason.SWARM_LARGER);
        } else {
          logger.info("Both Players have equal Points, no Winner");
          return null;
        }
      }
      return new WinCondition(PlayerColor.RED, WinReason.SWARM_CONNECTED);
    } else {
      logger.debug("Swarm is not connected for red");
      if(blueConnected) {
        logger.info("Swarm is connected for blue");
        return new WinCondition(PlayerColor.BLUE, WinReason.SWARM_CONNECTED);
      }
    }
    logger.debug("Swarm is not connected for blue");

    if(this.gameState.getTurn() == 2 * Constants.ROUND_LIMIT) {
      // round limit reached
      PlayerColor winner;
      if(stats[PlayerColor.RED.getIndex()][Constants.GAME_STATS_ROUNDS] > stats[PlayerColor.BLUE.getIndex()][Constants.GAME_STATS_ROUNDS]) {
        winner = PlayerColor.RED;
      } else if(stats[PlayerColor.RED.getIndex()][Constants.GAME_STATS_ROUNDS] < stats[PlayerColor.BLUE.getIndex()][Constants.GAME_STATS_ROUNDS]) {
        winner = PlayerColor.BLUE;
      } else {
        return null;
      }
      return new WinCondition(winner, WinReason.ROUND_LIMIT);
    }

    return null;
  }

  @Override
  public void loadFromFile(String file) {
    logger.info("Loading game from: " + file);
    GameLoader gl = new GameLoader(GameState.class);
    Object gameInfo = gl.loadGame(Configuration.getXStream(), file);
    if(gameInfo != null) {
      loadGameInfo(gameInfo);
    }
  }

  @Override
  public void loadFromFile(String file, int turn) {
    logger.info("Loading game from: " + file + " at turn: " + turn);
    // only copy right gameState specified by turn
    File tmp_replay = new File("./tmp_replay.xml");
    try {
      FileReader fileReader = new FileReader(file);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      FileWriter fileWriter = new FileWriter(tmp_replay);
      BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
      String line;
      bufferedWriter.write("<protocol>"); // XXX since hui18 replays start with protocol instead of object-stream
      bufferedWriter.newLine();
      while((line = bufferedReader.readLine()) != null) {
        if(line.contains("turn=\"" + turn + "\"")) {
          bufferedWriter.write(line);
          bufferedWriter.newLine();
          // case a gameState with specified turn was found
          while((line = bufferedReader.readLine()) != null
                  && !line.contains("turn=\"" + (turn + 1) + "\"")) {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
          }
        }

      }
      bufferedWriter.write("</protocol>");
      bufferedWriter.flush();
    } catch(IOException e) {
      e.printStackTrace();
    }
    GameLoader gl = new GameLoader(GameState.class);
    Object gameInfo = gl.loadGame(Configuration.getXStream(), "./tmp_replay.xml");
    if(gameInfo != null) {
      loadGameInfo(gameInfo);
    }
    // delete copied
    tmp_replay.delete();
  }

  // XXX test this
  @Override
  public void loadGameInfo(Object gameInfo) {
    logger.info("Processing game information");
    if(gameInfo instanceof GameState) {
      this.gameState = (GameState) gameInfo;
      // the currentPlayer has to be RED (else the DrawMove request is send to the
      // wrong player)
      // if it isn't red, the players have to be switched and red is made
      // currentPlayer
      if(this.gameState.getCurrentPlayerColor() != PlayerColor.RED) {
        this.gameState.setCurrentPlayerColor(PlayerColor.RED);
        Player newRed = this.gameState.getPlayer(PlayerColor.BLUE).clone();
        newRed.setColor(PlayerColor.RED);
        Player newBlue = this.gameState.getPlayer(PlayerColor.RED).clone();
        newBlue.setColor(PlayerColor.BLUE);
        this.gameState.setRed(newRed);
        this.gameState.setBlue(newBlue);
      }
    }
  }

  @Override
  public List<Player> getWinners() {
    WinCondition winCondition = checkWinCondition();
    List<Player> winners = new ArrayList<>();
    if(winCondition != null) {
      for(Player player : this.players) {
        if(player.getColor() == winCondition.getWinner()) {
          winners.add(player);
          break;
        }
      }
    } else {
      // No win condition met, player with highest score wins. Winning score is
      // determined by matchpoints ("Siegpunkte"). The winning player has 2
      // matchpoints. Find this player. If no player has 2 matchpoints, it is a
      // draw.
      for(Player player : this.players) {
        if(getScoreFor(player).getValues().get(0).intValueExact() == Constants.WIN_SCORE) {
          winners.add(player);
          break;
        }
      }
    }
    return winners;
  }

  /** Liste der Spieler. Reihenfolge: RED, BLUE */
  @Override
  public List<Player> getPlayers() {
    return gameState.getPlayers();
  }

  /** Liste der playerScores für jeden Spieler. Reihenfolge: RED, BLUE */
  @Override
  public List<PlayerScore> getPlayerScores() {
    List<PlayerScore> playerScores = new ArrayList<>();
    getPlayers().forEach(player -> playerScores.add(getScoreFor(player)));
    return playerScores;
  }

}
