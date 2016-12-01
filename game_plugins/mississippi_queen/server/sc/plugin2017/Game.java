package sc.plugin2017;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import sc.api.plugins.IPlayer;
import sc.api.plugins.exceptions.GameLogicException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.api.plugins.host.GameLoader;
import sc.framework.plugins.ActionTimeout;
import sc.framework.plugins.RoundBasedGameInstance;
import sc.plugin2017.util.Configuration;
import sc.plugin2017.util.Constants;
import sc.plugin2017.util.InvalidMoveException;
import sc.shared.PlayerScore;
import sc.shared.ScoreCause;

/**
 * Minimal game. Basis for new plugins. This class holds the game logic.
 *
 * @author Sven Casimir
 * @since Juni, 2010
 */
@XStreamAlias(value = "game")
public class Game extends RoundBasedGameInstance<Player> {
	private static Logger logger = LoggerFactory.getLogger(Game.class);

	@XStreamOmitField
	private List<PlayerColor> availableColors = new LinkedList<>();

	@XStreamOmitField
	private int halfturns = 0;

	private GameState gameState = new GameState();

	public GameState getGameState() {
		return this.gameState.getVisible();
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
		return this.gameState.getVisible(); // return only the for the players visible board
	}

	/**
	 * Someone did something, check out what it was (move maybe? Then check the
	 * move)
	 */
	@Override
	protected void onRoundBasedAction(IPlayer fromPlayer, Object data)
			throws GameLogicException {

		Player author = (Player) fromPlayer;

		/**
		 *  NOTE: Checking if right player sent move was already done by
		 *  {@link sc.framework.plugins.RoundBasedGameInstance#onAction(IPlayer, Object)}.
		 *  There is no need to do it here again.
		 */
		try {
			if (!(data instanceof Move)) {
				throw new InvalidMoveException(author.getDisplayName()
						+ " hat kein Zug-Objekt gesendet.");
			}

      final Move move = (Move) data;
      move.perform(this.gameState, author);
      this.gameState.prepareNextTurn(move);
      this.halfturns++;
			next(this.gameState.getCurrentPlayer());
		} catch (InvalidMoveException e) {
			author.setViolated(true);
			String err = "Ungueltiger Zug von '" + author.getDisplayName()
					+ "'.\n" + e.getMessage();
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
		if (this.players.size() >= GamePlugin.MAX_PLAYER_COUNT)
			throw new TooManyPlayersException();

		final Player player = new Player(this.availableColors.remove(0));
		this.players.add(player);
		this.gameState.addPlayer(player);

		return player;
	}

	@Override
	public void onPlayerLeft(IPlayer player) {
		if (!player.hasViolated()) {
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
			
			// TODO 
//			if (entry.getKey() == player) {
//        score.setCause(cause);
//        score.setValueAt(0, new BigDecimal(0));
//      } else {
//        score.setValueAt(0, new BigDecimal(2));
//      }
		}

		notifyOnGameOver(res);
	}

	@Override
	public boolean ready() {
		return this.players.size() == GamePlugin.MAX_PLAYER_COUNT;
	}

	@Override
	public void start() {
		for (final Player p : this.players) {
			p.notifyListeners(new WelcomeMessage(p.getPlayerColor()));
		}

		super.start();
	}

	@Override
	protected void onNewTurn() {

	}

	@Override
	protected PlayerScore getScoreFor(Player p) {

	  logger.debug("get score for player {}", p.getPlayerColor());
	  logger.debug("player violated: {}", p.hasViolated());
		int[] stats = this.gameState.getPlayerStats(p);
		int matchPoints = 1;
		int[] oppPoints = this.gameState.getPlayerStats(p.getPlayerColor()
				.opponent());
		WinCondition winCondition = checkWinCondition();
		String winningReason = null;
		if (winCondition != null) {
		  winningReason = winCondition.getReason();
		}
		if (stats[Constants.GAME_STATS_POINTS_INDEX] > oppPoints[Constants.GAME_STATS_POINTS_INDEX] 
		    || (stats[Constants.GAME_STATS_POINTS_INDEX] == oppPoints[Constants.GAME_STATS_POINTS_INDEX] 
		        && stats[Constants.GAME_STATS_PASSENGER_INDEX] > oppPoints[Constants.GAME_STATS_PASSENGER_INDEX]))
			matchPoints = 2;
		else if (stats[Constants.GAME_STATS_POINTS_INDEX] < oppPoints[Constants.GAME_STATS_POINTS_INDEX] 
		    || (stats[Constants.GAME_STATS_POINTS_INDEX] == oppPoints[Constants.GAME_STATS_POINTS_INDEX] 
		        && stats[Constants.GAME_STATS_PASSENGER_INDEX] < oppPoints[Constants.GAME_STATS_PASSENGER_INDEX]))
			matchPoints = 0;
		// FIXME score calculation is done at too many places and does not respect score definition but assumes a fixed schema (points and matchpoints).
		Player opponent = p.getPlayerColor().opponent() == PlayerColor.BLUE ? this.gameState.getBluePlayer() : this.gameState.getRedPlayer();
		if (opponent.hasViolated() && !p.hasViolated()) {
		  matchPoints = 2;
		}
		return p.hasViolated() ? new PlayerScore(ScoreCause.RULE_VIOLATION, p.getViolationReason(), 0,
				stats[Constants.GAME_STATS_POINTS_INDEX], stats[Constants.GAME_STATS_PASSENGER_INDEX]) : new PlayerScore(ScoreCause.REGULAR, winningReason,
				matchPoints, stats[Constants.GAME_STATS_POINTS_INDEX], stats[Constants.GAME_STATS_PASSENGER_INDEX]);

	}

	@Override
	protected ActionTimeout getTimeoutFor(Player player) {
		return new ActionTimeout(true, 10000l, 2000l);
	}

	/**
	 * checks if one player reached the goal with enough passengers
	 * @return the player who reached the goal or null if no player reached the goal
	 */
	private Player checkGoalReached() {
	  for (final Player player : this.players) {
      if (player.getField(this.gameState.getBoard()).getType() == FieldType.GOAL && player.getPassenger() >= 2 && player.getSpeed() == 1) {
        return player;
      }
	  }
	  return null;
	}

	/**
	 * Checks if a win condition in the current game state is met.
	 * @return WinCondition with winner and reason or null, if no win condition is yet met.
	 */
	public WinCondition checkWinCondition() {
	  if (this.gameState.getTurn() > 1) {
	    // XXX only for test
      //return new WinCondition(PlayerColor.BLUE, "Das Rundenlimit von 2 wurde erreicht.");
	  }
    int[][] stats = this.gameState.getGameStats();
    if (this.gameState.getTurn() >= 2 * Constants.ROUND_LIMIT) {
      // round limit reached
      PlayerColor winner = null;
      if (stats[Constants.GAME_STATS_RED_INDEX][Constants.GAME_STATS_POINTS_INDEX] 
          > stats[Constants.GAME_STATS_BLUE_INDEX][Constants.GAME_STATS_POINTS_INDEX]) {
        winner = PlayerColor.RED;
      } else if (stats[Constants.GAME_STATS_RED_INDEX][Constants.GAME_STATS_POINTS_INDEX] 
          < stats[Constants.GAME_STATS_BLUE_INDEX][Constants.GAME_STATS_POINTS_INDEX]) {
        winner = PlayerColor.BLUE;
      }
      return new WinCondition(winner, "Das Rundenlimit wurde erreicht.");
    } else if (checkGoalReached() != null) {
      // one player reached the goal
      PlayerColor winner = checkGoalReached().getPlayerColor();
      return new WinCondition(winner, "Das Spiel ist beendet.\nEin Spieler ist im Ziel");
    } else if (getActivePlayer() != this.gameState.getStartPlayer() &&
        Math.abs(this.gameState.getRedPlayer().getTile() - this.gameState.getBluePlayer().getTile()) > 3) {
      // a player is more than three tiles before the other player
      PlayerColor winner;
      if(this.gameState.getRedPlayer().getTile() > this.gameState.getBluePlayer().getTile()) {
        winner = PlayerColor.RED;
      } else {
        winner = PlayerColor.BLUE;
      }
      return new WinCondition(winner, "Das Spiel ist vorzeitig zu Ende.\nEin Spieler wurde abgehängt.");
    }
    return null;
	}

	@Override
	protected boolean checkGameOverCondition() {
	  return checkWinCondition() != null;
	}

	@Override
	public void loadFromFile(String file) {
		GameLoader gl = new GameLoader(new Class<?>[] { GameState.class });
		Object gameInfo = gl.loadGame(Configuration.getXStream(), file);
		if (gameInfo != null) {
			loadGameInfo(gameInfo);
		}
	}

	@Override
	public void loadGameInfo(Object gameInfo) {
		if (gameInfo instanceof GameState) {
			this.gameState = (GameState) gameInfo;
		}
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
	    // matchpoints. Find this player. If no player has 2 matchpoints, it is a draw.
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
