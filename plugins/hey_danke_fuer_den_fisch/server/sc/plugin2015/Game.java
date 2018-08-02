package sc.plugin2015;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.framework.plugins.AbstractPlayer;
import sc.api.plugins.exceptions.GameLogicException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.api.plugins.host.GameLoader;
import sc.framework.plugins.ActionTimeout;
import sc.framework.plugins.RoundBasedGameInstance;
import sc.plugin2015.util.Constants;
import sc.plugin2015.GameState;
import sc.plugin2015.Move;
import sc.plugin2015.Player;
import sc.plugin2015.PlayerColor;
import sc.plugin2015.WelcomeMessage;
import sc.plugin2015.util.Configuration;
import sc.plugin2015.util.InvalidMoveException;
import sc.shared.PlayerScore;
import sc.shared.ScoreCause;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

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
	private List<PlayerColor> availableColors = new LinkedList<PlayerColor>();

	private GameState gameState = new GameState();

	public GameState getGameState() {
		return gameState;
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
		return gameState;
	}

	/**
	 * Someone did something, check out what it was (move maybe? Then check the
	 * move)
	 */
	@Override
	protected void onRoundBasedAction(AbstractPlayer fromPlayer, Object data)
			throws GameLogicException {

		final Player author = (Player) fromPlayer;
		final MoveType expectedMoveType = gameState.getCurrentMoveType();
		final Player expectedPlayer = gameState.getCurrentPlayer();

		try {
			if (author.getPlayerColor() != expectedPlayer.getPlayerColor()) {
				throw new InvalidMoveException(author.getDisplayName()
						+ " war nicht am Zug");
			}

			if (!(data instanceof Move)) {
				throw new InvalidMoveException(author.getDisplayName()
						+ " hat kein Zug-Objekt gesendet");
			}

			final Move move = (Move) data;
			if (move.getMoveType() != expectedMoveType) {
				throw new InvalidMoveException(author.getDisplayName()
						+ " hat falschen Zug-Typ gesendet");
			}

			move.perform(gameState, expectedPlayer);
			gameState.prepareNextTurn(move);

			if (gameState.getTurn() >= 2 * Constants.ROUND_LIMIT) {

				gameState.clearEndGame();

				int[][] stats = gameState.getGameStats();
				PlayerColor winner = null;
				String winnerName = "Gleichstand nach Anzahl der Fische "
						+ "und nach Schollen.";
				if (stats[0][0] > stats[1][0]) {
					winner = PlayerColor.RED;
					winnerName = "Sieg nach Anzahl der Fische.";
				} else if (stats[0][0] < stats[1][0]) {
					winner = PlayerColor.BLUE;
					winnerName = "Sieg nach Anzahl der Fische.";
				} else {
					if (stats[0][1] > stats[1][1]) {
						winner = PlayerColor.RED;
						winnerName = "Sieg nach Schollen.";
					} else if (stats[0][1] < stats[1][1]) {
						winner = PlayerColor.BLUE;
						winnerName = "Sieg nach Schollen.";
					}
				}
				gameState.endGame(winner, "Das Rundenlimit wurde erreicht.\n"
						+ winnerName);
			} else {
				if (gameState.getCurrentMoveType() == MoveType.RUN
						&& gameState.getPossibleMoves().size() == 1
						&& gameState.getPossibleMoves(
								gameState.getOtherPlayerColor()).size() == 1) {

					gameState.clearEndGame();

					int[][] stats = gameState.getGameStats();
					PlayerColor winner = null;
					String winnerName = "Gleichstand nach Anzahl der Fische "
							+ "und nach Schollen.";
					if (stats[0][0] > stats[1][0]) {
						winner = PlayerColor.RED;
						winnerName = "Sieg nach Anzahl der Fische.";
					} else if (stats[0][0] < stats[1][0]) {
						winner = PlayerColor.BLUE;
						winnerName = "Sieg nach Anzahl der Fische.";
					} else {
						if (stats[0][1] > stats[1][1]) {
							winner = PlayerColor.RED;
							winnerName = "Sieg nach Schollen.";
						} else if (stats[0][1] < stats[1][1]) {
							winner = PlayerColor.BLUE;
							winnerName = "Sieg nach Schollen.";
						}
					}

					gameState.endGame(winner,
							"Das Spiel ist vorzeitig zu Ende.\n"
									+ "Beide Spieler sind zugunfaehig. "
									+ winnerName);

				}
			}

			next(gameState.getCurrentPlayer());

		} catch (InvalidMoveException e) {
			author.setViolated(true);
			String err = "Ungültiger Zug von '" + author.getDisplayName()
					+ "'.\n" + e.getMessage() + ".";
			gameState.endGame(author.getPlayerColor().opponent(), err);
			logger.error(err);
			throw new GameLogicException(err);
		}
	}

	@Override
	public AbstractPlayer onPlayerJoined() throws TooManyPlayersException {
		if (this.players.size() >= GamePlugin.MAX_PLAYER_COUNT)
			throw new TooManyPlayersException();

		final Player player = new Player(this.availableColors.remove(0));
		this.players.add(player);
		this.gameState.addPlayer(player);

		return player;
	}

	@Override
	public void onPlayerLeft(AbstractPlayer player) {
		if (!player.hasViolated()) {
			onPlayerLeft(player, ScoreCause.LEFT);
		} else {
			onPlayerLeft(player, ScoreCause.RULE_VIOLATION);
		}
	}

	@Override
	public void onPlayerLeft(AbstractPlayer player, ScoreCause cause) {
		Map<AbstractPlayer, PlayerScore> res = generateScoreMap();

		for (Entry<AbstractPlayer, PlayerScore> entry : res.entrySet()) {
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
		if (stats[0] > oppPoints[0])
			matchPoints = 2;
		else if (stats[0] < oppPoints[0])
			matchPoints = 0;
		else if (stats[1] > oppPoints[1])
			matchPoints = 2;
		else if (stats[1] < oppPoints[1])
			matchPoints = 0;
		return p.hasViolated() ? new PlayerScore(ScoreCause.RULE_VIOLATION, 0,
				stats[0], stats[1]) : new PlayerScore(ScoreCause.REGULAR,
				matchPoints, stats[0], stats[1]);

	}

	@Override
	protected ActionTimeout getTimeoutFor(Player player) {
		return new ActionTimeout(true, 10000l, 2000l);
	}

	@Override
	protected boolean checkGameOverCondition() {
		return gameState.gameEnded();
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
	public List<AbstractPlayer> getWinners() {
		if (gameState.gameEnded()) {
			List<AbstractPlayer> winners = new LinkedList<AbstractPlayer>();
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
