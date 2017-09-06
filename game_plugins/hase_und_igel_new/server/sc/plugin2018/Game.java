package sc.plugin2018;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import sc.api.plugins.exceptions.GameLogicException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.api.plugins.host.GameLoader;
import sc.framework.plugins.ActionTimeout;
import sc.framework.plugins.RoundBasedGameInstance;
import sc.framework.plugins.SimplePlayer;
import sc.shared.PlayerColor;
import sc.plugin2018.util.Configuration;
import sc.plugin2018.util.Constants;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerScore;
import sc.shared.ScoreCause;
import sc.shared.WinCondition;

/**
 * Minimal game. Basis for new plugins. This class holds the game logic.
 */
@XStreamAlias(value = "game")
public class Game extends RoundBasedGameInstance<Player>
{
	private static Logger			logger			= LoggerFactory
															.getLogger(Game.class);

	@XStreamOmitField
	private List<PlayerColor>		availableColors	= new LinkedList<>();

  private GameState gameState = new GameState();

  public GameState getGameState() {
    return this.gameState;
  }

  public Game() {
    this.availableColors.add(PlayerColor.RED);
    this.availableColors.add(PlayerColor.BLUE);
  }

  @Override
  protected Object getCurrentState() {
    return this.gameState; // return visible board for the players
  }

  /**
   * Someone did something, check out what it was (move maybe? Then check the
   * move)
   */
  @Override
  protected void onRoundBasedAction(SimplePlayer fromPlayer, Object data) throws GameLogicException {

    Player author = (Player) fromPlayer;

    /*
     * NOTE: Checking if right player sent move was already done by
     * {@link sc.framework.plugins.RoundBasedGameInstance#onAction(SimplePlayer, Object)}.
     * There is no need to do it here again.
     */
    try {
      if (!(data instanceof Move)) {
        throw new InvalidMoveException(author.getDisplayName() + " hat kein Zug-Objekt gesendet.");
      }

      final Move move = (Move) data;
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
  @Override
  protected boolean increaseTurnIfNecessary(Player nextPlayer) {
    return getGameState().getTurn() % 2 == 0;
  }

  @Override
  public SimplePlayer onPlayerJoined() throws TooManyPlayersException {
    final Player player;
    // When starting a game from a imported state the players should not be
    // overwritten
    PlayerColor playerColor = this.availableColors.remove(0);
    if (PlayerColor.RED == playerColor && this.gameState.getRedPlayer() != null) {
      player = this.gameState.getRedPlayer();
    } else if (PlayerColor.BLUE == playerColor && this.gameState.getBluePlayer() != null) {
      player = this.gameState.getBluePlayer();
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
  protected PlayerScore getScoreFor(Player player) {

    logger.debug("get score for player {}", player.getPlayerColor());
    logger.debug("player violated: {}", player.hasViolated());
    int[] stats = this.gameState.getPlayerStats(player);
    int matchPoints = Constants.DRAW_SCORE;
    WinCondition winCondition = checkWinCondition();
    String reason = null;
    Player opponent = player.getPlayerColor().opponent() == PlayerColor.BLUE ? this.gameState.getBluePlayer()
        : this.gameState.getRedPlayer();
    if (winCondition != null) {
      reason = winCondition.getReason();
      if(player.getPlayerColor().equals(winCondition.getWinner())) {
        matchPoints = 2;
      } else if (opponent.getPlayerColor().equals(winCondition.getWinner())) {
        matchPoints = 0;
      } else {
        // this should not happen
        logger.warn("winner has no known PlayerColor");
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
      matchPoints = 0;
    } else if (player.hasHardTimeout()) { // Hard-Timeout
      cause = ScoreCause.HARD_TIMEOUT;
      matchPoints = 0;
    } else if (player.hasViolated()) { // rule violation
      cause = ScoreCause.RULE_VIOLATION;
      reason = player.getViolationReason(); // message from InvalidMoveException
      matchPoints = 0;
    } else if (player.hasLeft()) { // player left
      cause = ScoreCause.LEFT;
      matchPoints = 0;
    } else { // regular score or opponent violated
      cause = ScoreCause.REGULAR;
    }
    return new PlayerScore(cause, reason, matchPoints, stats[Constants.GAME_STATS_FIELD_INDEX],
        stats[Constants.GAME_STATS_CARROTS]);
  }

	@Override
	protected ActionTimeout getTimeoutFor(Player player)
	{
		return new ActionTimeout(true, 10000L, 2000L);
	}

  /**
   * Checks if a win condition in the current game state is met.
   * Checks round limit and end of round (and playerStats).
   * Checks if goal is reached
   *
   * @return WinCondition with winner and reason or null, if no win condition is
   *         yet met.
   */
  public WinCondition checkWinCondition() {
    int[][] stats = this.gameState.getGameStats();
    if (this.gameState.getTurn() <= 2 * Constants.ROUND_LIMIT) {
      // round limit not reached
      Player winningPlayer = checkGoalReached();
      if (winningPlayer != null){
        return new WinCondition(winningPlayer.getPlayerColor(), Constants.IN_GOAL_MESSAGE);
      } else {
        return null;
      }

    } else { // this.gameState.getTurn() >= 2 * Constants.ROUND_LIMIT
      // round limit reached
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

  /**
   * Checks if one player reached the goal (at the end of a round). If both player are in goal, the one with lesser carrots
   * wins, if they both have the same amount, red wins for first entering goal.
   *
   * @return the player who reached the goal or null if no player reached the
   *         goal, only returns a player on the end of a round else always null
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
	public void loadFromFile(String file)
	{
		logger.info("Loading game from: " + file);
		GameLoader gl = new GameLoader(new Class<?>[] {GameState.class});
		Object gameInfo = gl.loadGame(Configuration.getXStream(), file);
		if (gameInfo != null) {
			loadGameInfo(gameInfo);
		}
	}

  @Override
  public void loadFromFile(String file, int turn)
  {
    logger.info("Loading game from: " + file + " at turn: " + turn);
    // only copy right gameState specified by turn
    try {
      FileReader fileReader = new FileReader(file);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      FileWriter fileWriter = new FileWriter("./tmp_replay.xml");
      BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
      String line;
      bufferedWriter.write("<object-stream>");
      bufferedWriter.newLine();
      while((line = bufferedReader.readLine()) != null) {
        if (line.contains("turn=\"" + turn +  "\"")) {
          bufferedWriter.write(line);
          bufferedWriter.newLine();
          // case a gameState with specified turn was found
          while((line = bufferedReader.readLine()) != null
                  &&!line.contains("turn=\"" + (turn + 1) +  "\"")) {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
          }
        }

      }
      bufferedWriter.write("</object-stream>");
      bufferedWriter.flush();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    GameLoader gl = new GameLoader(new Class<?>[] {GameState.class});
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
	public void loadGameInfo(Object gameInfo)
	{
		logger.info("Processing game information");
    if (gameInfo instanceof GameState) {
      this.gameState = (GameState) gameInfo;
      // when loading from a state the listeners are not initialized
      this.gameState.getRedPlayer().initListeners();
      this.gameState.getBluePlayer().initListeners();
      // the currentPlayer has to be RED (else the Move request is send to the
      // wrong player)
      // if it isn't red, the players have to be switched and red is made
      // currentPlayer
      if (this.gameState.getCurrentPlayerColor() != PlayerColor.RED) {
        this.gameState.setCurrentPlayer(PlayerColor.RED);
        Player newRed = this.gameState.getBluePlayer().clone();
        newRed.setPlayerColor(PlayerColor.RED);
        Player newBlue = this.gameState.getRedPlayer().clone();
        newBlue.setPlayerColor(PlayerColor.BLUE);
        this.gameState.setRedPlayer(newRed);
        this.gameState.setBluePlayer(newBlue);
      }
    }
  }

  @Override
  public List<SimplePlayer> getWinners() {
    WinCondition win = checkWinCondition();
    List<SimplePlayer> winners = new LinkedList<>();
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
}
