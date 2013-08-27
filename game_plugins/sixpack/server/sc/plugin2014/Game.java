package sc.plugin2014;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.IPlayer;
import sc.api.plugins.exceptions.GameLogicException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.api.plugins.host.GameLoader;
import sc.framework.plugins.ActionTimeout;
import sc.framework.plugins.RoundBasedGameInstance;
import sc.plugin2014.entities.Player;
import sc.plugin2014.entities.PlayerColor;
import sc.plugin2014.entities.Stone;
import sc.plugin2014.exceptions.InvalidMoveException;
import sc.plugin2014.exceptions.StoneBagIsEmptyException;
import sc.plugin2014.moves.Move;
import sc.plugin2014.util.Constants;
import sc.plugin2014.util.XStreamConfiguration;
import sc.shared.PlayerScore;
import sc.shared.ScoreCause;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Minimal game. Basis for new plugins. This class holds the game logic.
 * 
 */
@XStreamAlias(value = "game")
public class Game extends RoundBasedGameInstance<Player> {
	private static Logger logger = LoggerFactory.getLogger(Game.class);

	@XStreamOmitField
	private final List<PlayerColor> availableColors = new LinkedList<PlayerColor>();

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
	protected void onRoundBasedAction(IPlayer fromPlayer, Object data)
			throws GameLogicException {

		final Player author = (Player) fromPlayer;
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

			move.perform(gameState, expectedPlayer);
			gameState.prepareNextTurn(move);

			if (gameState.getTurn() >= (2 * Constants.ROUND_LIMIT)) {
				endGameRegularly("Das Rundenlimit wurde erreicht.");
			}

			if (expectedPlayer.getStones().isEmpty()) {
				endGameRegularly("Ein Spieler hat keine Steine mehr.");
			}

			next(gameState.getCurrentPlayer());

		} catch (InvalidMoveException e) {
			author.setViolated(true);
			String err = "Ungültiger Zug von '" + author.getDisplayName()
					+ "'.\\n" + e.getMessage() + ".";
			gameState.endGame(author.getPlayerColor().getOpponent(), err);
			logger.error(err);
			throw new GameLogicException(err);
		} catch (StoneBagIsEmptyException e) {
			gameState.prepareNextTurn((Move) data);
			endGameRegularly("Der Beutel ist leer.");
			next(gameState.getCurrentPlayer());
		}
	}

	private void endGameRegularly(String message) {
		int[][] stats = gameState.getGameStats();
		PlayerColor winner = null;
		String winnerName = "Gleichstand nach Punkten.";
		if (stats[0][0] > stats[1][0]) {
			winner = PlayerColor.RED;
			winnerName = "Sieg nach Punkten.";
		} else if (stats[0][0] < stats[1][0]) {
			winner = PlayerColor.BLUE;
			winnerName = "Sieg nach Punkten.";
		}
		gameState.endGame(winner, message + "\\n" + winnerName);
	}

	@Override
	public IPlayer onPlayerJoined() throws TooManyPlayersException {
		if (players.size() >= GamePlugin.MAX_PLAYER_COUNT) {
			throw new TooManyPlayersException();
		}

		final Player player = new Player(availableColors.remove(0));
		players.add(player);
		gameState.addPlayer(player);

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
				score.setValueAt(0, new BigDecimal(0));
			} else {
				score.setValueAt(0, new BigDecimal(2));
			}
		}

		if (!gameState.gameEnded()) {
			gameState.endGame(((Player) player).getPlayerColor().getOpponent(),
					"Der Spieler '" + player.getDisplayName()
							+ "' hat das Spiel verlassen.");
		}

		notifyOnGameOver(res);
	}

	@Override
	public boolean ready() {
		return players.size() == GamePlugin.MAX_PLAYER_COUNT;
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
		int oppPoints = gameState.getPlayerStats(p.getPlayerColor()
				.getOpponent())[0];
		if (stats[0] > oppPoints) {
			matchPoints = 2;
		} else if (stats[0] < oppPoints) {
			matchPoints = 0;
		}
		return p.hasViolated() ? new PlayerScore(ScoreCause.RULE_VIOLATION, -2,
				stats[0]) : new PlayerScore(ScoreCause.REGULAR, matchPoints,
				stats[0]);

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
		Object gameInfo = gl.loadGame(XStreamConfiguration.getXStream(), file);
		if (gameInfo != null) {
			loadGameInfo(gameInfo);
		}
	}

	@Override
	public void loadGameInfo(Object gameInfo) {
		//TODO Player Points and Turn and CurrentPlayer
		if (gameInfo instanceof GameState) {
			GameState temp = (GameState) gameInfo;
			gameState.loadFromFile(temp);
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
