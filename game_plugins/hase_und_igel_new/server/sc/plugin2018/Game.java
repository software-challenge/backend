package sc.plugin2018;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.IPlayer;
import sc.api.plugins.exceptions.GameLogicException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.api.plugins.host.GameLoader;
import sc.framework.plugins.ActionTimeout;
import sc.framework.plugins.RoundBasedGameInstance;
import sc.plugin2018.util.InvalidMoveException;
import sc.plugin2018.GamePlugin;
import sc.plugin2018.WinCondition;
import sc.plugin2018.util.Constants;
import sc.plugin2018.util.GameUtil;
import sc.plugin2018.Board;
import sc.plugin2018.PlayerColor;
import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.plugin2018.Player;
import sc.plugin2018.Position;
import sc.plugin2018.WelcomeMessage;
import sc.plugin2018.util.Configuration;
import sc.shared.PlayerScore;
import sc.shared.ScoreCause;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Minimal game. Basis for new plugins. This class holds the game logic.
 */
@XStreamAlias(value = "game")
public class Game extends RoundBasedGameInstance<Player>
{
	private static Logger			logger			= LoggerFactory
															.getLogger(Game.class);

	@XStreamOmitField
	private List<PlayerColor>		availableColors	= new LinkedList<PlayerColor>();

  @XStreamOmitField
  private int halfturns = 0;

  private GameState gameState = new GameState();

  public GameState getGameState() {
    return this.gameState;
  }

  public Player getActivePlayer() {
    return this.activePlayer;
  }

  public Game() {
    this.availableColors.add(PlayerColor.RED);
    this.availableColors.add(PlayerColor.BLUE);
  }

  @Override
  protected Object getCurrentState() {
    return this.gameState.getVisible(); // return only the for the players
                                        // visible board
  }

	@Override
	protected boolean checkGameOverCondition()
	{
	  // you can only win at the end of a round
	  // check if salads are used
	  // check if less than 10 carrots
		return false;
	}

  /**
   * Someone did something, check out what it was (move maybe? Then check the
   * move)
   */
  @Override
  protected void onRoundBasedAction(IPlayer fromPlayer, Object data) throws GameLogicException {

    Player author = (Player) fromPlayer;

    /**
     * NOTE: Checking if right player sent move was already done by
     * {@link sc.framework.plugins.RoundBasedGameInstance#onAction(IPlayer, Object)}.
     * There is no need to do it here again.
     */
    try {
      if (!(data instanceof Move)) {
        throw new InvalidMoveException(author.getDisplayName() + " hat kein Zug-Objekt gesendet.");
      }

      final Move move = (Move) data;
      move.perform(this.gameState, author);
      this.gameState.prepareNextTurn(move);
      this.halfturns++;
      next(this.gameState.getCurrentPlayer());
      onPlayerChange(this.getGameState().getOtherPlayer()); // check this
    } catch (InvalidMoveException e) {
      author.setViolated(true);
      String err = "Ungueltiger Zug von '" + author.getDisplayName() + "'.\n" + e.getMessage();
      author.setViolationReason(e.getMessage());
      logger.error(err, e);
      throw new GameLogicException(err);
    }
  }
  
  /**
   * In this game, a new turn begins when both players made one move. The order
   * in which the players make their move may change.
   */
  @Override
  protected boolean increaseTurnIfNecessary(Player nextPlayer) {
    return getGameState().getTurn() % 2 == 0;
  }

  @Override
  public IPlayer onPlayerJoined() throws TooManyPlayersException {
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
  
  @Override
  public void onPlayerLeft(IPlayer player) {
    if (!player.hasViolated()) {
      player.setLeft(true);
      onPlayerLeft(player, ScoreCause.LEFT);
    } else {
      onPlayerLeft(player, ScoreCause.RULE_VIOLATION);
    }
  }

  @Override
  public void onPlayerLeft(IPlayer player, ScoreCause cause) {
    Map<IPlayer, PlayerScore> res = generateScoreMap();

    for (Entry<IPlayer, PlayerScore> entry : res.entrySet()) {
      PlayerScore score = entry.getValue();

      if (entry.getKey() == player) {
        score.setCause(cause);
      }
    }

    notifyOnGameOver(res);
  }

  @Override
  public boolean ready() {
    return this.players.size() == GamePlugin.MAX_PLAYER_COUNT;
  }

	private void onPlayerChange(Player player)
	{
		switch (this.gameState.getBoard().getTypeAt(player.getFieldIndex()))
		{
			case POSITION_1:
				if (this.gameState.getBoard().isFirst(player))
					player.changeCarrotsAvailableBy(10);
				break;
			case POSITION_2:
				if (!this.gameState.getBoard().isFirst(player))
					player.changeCarrotsAvailableBy(30);
				break;
			default:
				break;
		}
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
	protected void onNewTurn()
	{
	}

	// TODO add right calculation
	@Override
  protected PlayerScore getScoreFor(Player p) {

    logger.debug("get score for player {}", p.getPlayerColor());
    logger.debug("player violated: {}", p.hasViolated());
    int[] stats = this.gameState.getPlayerStats(p);
    int matchPoints = 1;
    int[] oppPoints = this.gameState.getPlayerStats(p.getPlayerColor().opponent());
    WinCondition winCondition = checkWinCondition();
    String reason = null;
    Player opponent = p.getPlayerColor().opponent() == PlayerColor.BLUE ? this.gameState.getBluePlayer()
        : this.gameState.getRedPlayer();
    if (winCondition != null) {
      reason = winCondition.getReason();
      if(p.equals(winCondition.getWinner())) {
        matchPoints = 2;
      } else if (opponent.equals(winCondition.getWinner())) {
        matchPoints = 0;
      } else {
        // this should not happen
      }
      // TODO check winner set matchpoints etc
    } else if (stats[Constants.GAME_STATS_FIELD_INDEX] > oppPoints[Constants.GAME_STATS_FIELD_INDEX]
        || (stats[Constants.GAME_STATS_FIELD_INDEX] == oppPoints[Constants.GAME_STATS_FIELD_INDEX]
            && stats[Constants.GAME_STATS_CARROTS] > oppPoints[Constants.GAME_STATS_CARROTS])) {
      matchPoints = 2;
    }
    else if (stats[Constants.GAME_STATS_FIELD_INDEX] < oppPoints[Constants.GAME_STATS_FIELD_INDEX]
        || (stats[Constants.GAME_STATS_FIELD_INDEX] == oppPoints[Constants.GAME_STATS_FIELD_INDEX]
            && stats[Constants.GAME_STATS_CARROTS] < oppPoints[Constants.GAME_STATS_CARROTS])) {
      matchPoints = 0; 
    }
    // opponent has done something wrong
    if (opponent.hasViolated() && !p.hasViolated() || opponent.hasLeft() && !p.hasLeft() 
        || opponent.hasSoftTimeout() || opponent.hasHardTimeout()) {
      matchPoints = 2;
    }
    ScoreCause cause;
    if (p.hasSoftTimeout()) { // Soft-Timeout
      cause = ScoreCause.SOFT_TIMEOUT;
      matchPoints = 0;
    } else if (p.hasHardTimeout()) { // Hard-Timeout
      cause = ScoreCause.HARD_TIMEOUT;
      matchPoints = 0;
    } else if (p.hasViolated()) { // rule violation
      cause = ScoreCause.RULE_VIOLATION;
      reason = p.getViolationReason(); // message from InvalidMoveException
      matchPoints = 0;
    } else if (p.hasLeft()) { // player left
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
		return new ActionTimeout(true, 10000l, 2000l);
	}

  /**
   * Checks if a win condition in the current game state is met.
   *
   * @return WinCondition with winner and reason or null, if no win condition is
   *         yet met.
   */
  public WinCondition checkWinCondition() {
    int[][] stats = this.gameState.getGameStats();
    if (this.gameState.getTurn() >= 2 * Constants.ROUND_LIMIT) {
      // round limit reached
      PlayerColor winner = null;
      if (stats[Constants.GAME_STATS_RED_INDEX][Constants.GAME_STATS_FIELD_INDEX] > stats[Constants.GAME_STATS_BLUE_INDEX][Constants.GAME_STATS_FIELD_INDEX]) {
        winner = PlayerColor.RED;
      } else if (stats[Constants.GAME_STATS_RED_INDEX][Constants.GAME_STATS_FIELD_INDEX] < stats[Constants.GAME_STATS_BLUE_INDEX][Constants.GAME_STATS_FIELD_INDEX]) {
        winner = PlayerColor.BLUE;
      } else {
        if (stats[Constants.GAME_STATS_RED_INDEX][Constants.GAME_STATS_CARROTS] < stats[Constants.GAME_STATS_BLUE_INDEX][Constants.GAME_STATS_CARROTS]) {
          winner = PlayerColor.BLUE;  
        } else {
          // red wins on draw, because red first entered the goal
          winner = PlayerColor.RED;
        }
      }
      return new WinCondition(winner, "Das Rundenlimit wurde erreicht.");
    } else if (checkGoalReached() != null) {
      // one player reached the goal
      PlayerColor winner = checkGoalReached().getPlayerColor();
      return new WinCondition(winner, "Das Spiel ist beendet.\nEin Spieler ist im Ziel");
    }
    return null;
  }
	
  /**
   * checks if one player reached the goal
   *
   * @return the player who reached the goal or null if no player reached the
   *         goal, only returns a player on the end of a round else always null
   */
  private Player checkGoalReached() {
    if (this.halfturns % 2 == 1) {
      Player red = this.gameState.getRedPlayer();
      Player blue = this.gameState.getBluePlayer();
      if (red.inGoal()) {
        if (blue.inGoal() && red.getCarrotsAvailable() < blue.getCarrotsAvailable()) {
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
		GameLoader gl = new GameLoader(new Class<?>[] {GameState.class, Board.class});
		Object gameInfo = gl.loadGame(Configuration.getXStream(), file);
		if (gameInfo != null) {
			loadGameInfo(gameInfo);
		}
	}

	@Override
	public void loadGameInfo(Object gameInfo)
	{
		logger.info("Processing game information");
		// TODO
	}

	 @Override
	  public List<IPlayer> getWinners() {
	    WinCondition win = checkWinCondition();
	    List<IPlayer> winners = new LinkedList<IPlayer>();
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
