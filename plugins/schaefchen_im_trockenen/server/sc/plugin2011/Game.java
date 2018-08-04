package sc.plugin2011;

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
import sc.plugin2011.BoardFactory;
import sc.plugin2011.GameState;
import sc.plugin2011.Move;
import sc.plugin2011.Player;
import sc.plugin2011.PlayerColor;
import sc.plugin2011.WelcomeMessage;
import sc.plugin2011.util.Configuration;
import sc.plugin2011.util.Constants;
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
@XStreamAlias(value = "sit:game")
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

	protected void onActivePlayerChanged(Player player) {
		gameState.setCurrentPlayer(player.getPlayerColor());
	}

	public Game() {
		availableColors.add(PlayerColor.RED);
		availableColors.add(PlayerColor.BLUE);
	}

	@Override
	protected Object getCurrentState() {
		gameState.setCurrentPlayer(activePlayer.getPlayerColor());
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

		if (data instanceof Move) {
			final Move move = (Move) data;

			// auf fehlerhafte zuege ueberpruefen
			String err = null;
			if (move.target < 0 || move.target >= BoardFactory.nodes.size()) {
				err = "Es gibt kein Feld #" + move.target;
			} else if (gameState.getSheep(move.sheep) == null) {
				err = "Es gibt kein Schaf #" + move.sheep;
			} else if (!gameState.getSheep(move.sheep).owner
					.equals(getActivePlayer().getPlayerColor())) {
				err = "Der aktuelle Spieler darf Schaf #" + move.sheep
						+ "nicht bewegen";
			} else if (!gameState.getReacheableNodes(
					gameState.getSheep(move.sheep)).contains(move.target)) {
				err = "Schaf #" + move.sheep + " kann das Feld #" + move.target
						+ " nicht erreichen";
			} else if (!gameState.isValidTarget(move.sheep, move.target)) {
				err = "Schaf #" + move.sheep + " darf das Feld #" + move.target
						+ " nicht betreten";
			}

			// fehlerhgaften zug melden
			if (err != null) {
				author.setViolated(true);
				err = "Ungültiger Zug von '" + author.getDisplayName()
						+ "'.\\n" + err + ".";
				logger.error(err);
				gameState.endGame(author.getPlayerColor().opponent(), err);
				throw new GameLogicException(err);

			}

			// korrekten zug ausfuehren
			gameState.performMove(move);

			// wurde durch diesen zug das spiel gewonnen?
			if (gameState.getSheeps(author.getPlayerColor().opponent()).size() == 0) {
				gameState
						.endGame(
								author.getPlayerColor(),
								"Das Spiel ist vorzeitig zu Ende.\\n'"
										+ (gameState.getPlayerNames()[author
												.getPlayerColor() == PlayerColor.RED ? 1
												: 0])
										+ "' hat keine Schafe mehr.");
				System.out.println(" ***** vorzeitig zu ende");

			} else if (gameState.getTurn() >= 2 * Constants.ROUND_LIMIT) {
				int[][] stats = gameState.getGameStats();
				PlayerColor winner = null;
				String winnerName = "Gleichstand nach Punkten.";
				if (stats[0][6] > stats[1][6]) {
					winner = PlayerColor.RED;
					winnerName = "Sieg nach Punkten.";
				} else if (stats[0][6] < stats[1][6]) {
					winner = PlayerColor.BLUE;
					winnerName = "Sieg nach Punkten.";
				}
				gameState.endGame(winner, "Das Rundenlimit wurde erreicht.\\n"
						+ winnerName);
			}

			// neuer wuerfel und naechster spieler
			gameState.rollDice();
			next();

		} else {
			logger.error("Received unexpected {} from {}.", data, author);
			throw new GameLogicException("Unknown ObjectType received.");
		}
	}

	@Override
	public AbstractPlayer onPlayerJoined() throws TooManyPlayersException {
		if (this.players.size() >= GamePlugin.MAX_PLAYER_COUNT)
			throw new TooManyPlayersException();

		final Player player = new Player(this.availableColors.remove(0));
		this.players.add(player);
		this.gameState.setPlayer(player);

		return player;
	}

	@Override
	protected void next() {
		int activePlayerId = this.players.indexOf(this.activePlayer);
		activePlayerId = (activePlayerId + 1) % this.players.size();
		final Player nextPlayer = this.players.get(activePlayerId);
		next(nextPlayer);
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
				score.setCause(cause);
				score.setValueAt(0, new BigDecimal(0));
			} else {
				score.setValueAt(0, new BigDecimal(2));
			}
		}
		
		if(!gameState.gameEnded()) {
			gameState
			.endGame(
					((Player)player).getPlayerColor().opponent(),
					"Der Spieler '"
							+ player.getDisplayName()
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

		int[] stats = gameState.getPlayerStats(p.getPlayerColor());
		return p.hasViolated() ? new PlayerScore(ScoreCause.RULE_VIOLATION, 0,
				0, 0, 0, 0, 0, 0) : new PlayerScore(ScoreCause.REGULAR,
				stats[0], stats[1], stats[2], stats[3], stats[4], stats[5],
				stats[6]);

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
				for(Player player : players) {
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
