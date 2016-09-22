package sc.plugin2017;

import java.math.BigDecimal;
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
		return gameState.getVisible();
	}

	public Player getActivePlayer() {
		return activePlayer;
	}

	public Game() {
		availableColors.add(PlayerColor.RED);
		availableColors.add(PlayerColor.BLUE);
	}

	@Override
	protected Object getCurrentState() {
		return gameState.getVisible(); // return only the for the players visible board
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
      move.perform(gameState, author);
      gameState.prepareNextTurn(move);
      halfturns++;
			next(gameState.getCurrentPlayer());
		} catch (InvalidMoveException e) {
			author.setViolated(true);
			String err = "Ungueltiger Zug von '" + author.getDisplayName()
					+ "'.\n" + e.getMessage();
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
				logger.debug("setting 0 score");
				score.setCause(cause);
				score.setValueAt(0, new BigDecimal(0));
			} else {
				score.setValueAt(0, new BigDecimal(2));
			}
		}

		if (!gameState.gameEnded()) {
			gameState.endGame(((Player) player).getPlayerColor().opponent(),
					"Der Spieler '" + player.getDisplayName()
							+ "' hat das Spiel verlassen.");
		}
		notifyOnGameOver(res);
	}

	@Override
	public boolean ready() {
		return this.players.size() == GamePlugin.MAX_PLAYER_COUNT;
	}

	@Override
	public void start() {
		for (final Player p : players) {
			p.notifyListeners(new WelcomeMessage(p.getPlayerColor()));
		}

		super.start();
	}

	@Override
	protected void onNewTurn() {

	}

	@Override
	protected PlayerScore getScoreFor(Player p) {

		int[] stats = gameState.getPlayerStats(p);
		int matchPoints = 1;
		int[] oppPoints = gameState.getPlayerStats(p.getPlayerColor()
				.opponent());
		if (stats[0] > oppPoints[0] || (stats[0] == oppPoints[0] && stats[1] > oppPoints[1]))
			matchPoints = 2;
		else if (stats[0] < oppPoints[0] || (stats[0] == oppPoints[0] && stats[1] < oppPoints[1]))
			matchPoints = 0;
		return p.hasViolated() ? new PlayerScore(ScoreCause.RULE_VIOLATION, 0,
				stats[0]) : new PlayerScore(ScoreCause.REGULAR,
				matchPoints, stats[0]);

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
	  Player reached = null;
	  for (final Player player : players) {
      if (player.getField(gameState.getBoard()).getType() == FieldType.GOAL && player.getPassenger() >= 2) {
        reached = player;
      }
	  }
	  return reached;
	}

	@Override
	protected boolean checkGameOverCondition() {
	  if (gameState.gameEnded()) {
	    // game was ended by invalid move
	    return true;
	  }
    int[][] stats = gameState.getGameStats();
    if (gameState.getTurn() >= 2 * Constants.ROUND_LIMIT) {
      // round limit reached
      PlayerColor winner = null;
      if (stats[0][0] > stats[1][0]) {
        winner = PlayerColor.RED;
      } else if (stats[0][0] < stats[1][0]) {
        winner = PlayerColor.BLUE;
      }
      gameState.endGame(winner, "Das Rundenlimit wurde erreicht.");
      return true;
    } else if (checkGoalReached() != null) {
      // one player reached the goal
      PlayerColor winner = checkGoalReached().getPlayerColor();
      gameState.endGame(winner, "Das Spiel beendet.\nEin Spieler ist im Ziel");
      return true;
    } else if (getActivePlayer() != gameState.getStartPlayer() &&
        Math.abs(gameState.getRedPlayer().getTile() - gameState.getBluePlayer().getTile()) > 3) {
      // a player is more than three tiles before the other player
      PlayerColor winner;
      if(gameState.getRedPlayer().getTile() > gameState.getBluePlayer().getTile()) {
        winner = PlayerColor.RED;
      } else {
        winner = PlayerColor.BLUE;
      }
      gameState.endGame(winner, "Das Spiel ist vorzeitig zu Ende.\nEin Spieler wurde abgehängt.");
      return true;
    }
    return false;
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
		if (gameState.gameEnded()) {
			List<IPlayer> winners = new LinkedList<IPlayer>();
			if (gameState.winner() != null) {
				for (Player player : players) {
					if (player.getPlayerColor() == gameState.winner()) {
						winners.add(player);
						break;
					}
				}
			}
			return winners;
		} else {
			return null;
		}
	}

}
