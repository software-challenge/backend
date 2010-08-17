package sc.plugin_schaefchen;

import java.math.BigDecimal;
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
import sc.plugin_schaefchen.Board;
import sc.plugin_schaefchen.GameState;
import sc.plugin_schaefchen.Move;
import sc.plugin_schaefchen.Player;
import sc.plugin_schaefchen.PlayerColor;
import sc.plugin_schaefchen.WelcomeMessage;
import sc.plugin_schaefchen.util.Configuration;
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

	private Board board = new Board();

	public Board getBoard() {
		return board;
	}

	public Player getActivePlayer() {
		return activePlayer;
	}

	public Game() {
		availableColors.add(PlayerColor.PLAYER1);
		availableColors.add(PlayerColor.PLAYER2);
	}

	@Override
	protected Object getCurrentState() {
		return new GameState(this);
	}

	/**
	 * Someone did something, check out what it was (move maybe? Then check the
	 * move)
	 */
	@Override
	protected void onRoundBasedAction(IPlayer fromPlayer, Object data)
			throws GameLogicException {
		final Player author = (Player) fromPlayer;

		// Player did a move
		if (data instanceof Move) {
			final Move move = (Move) data;

			if (author != getActivePlayer()) {
				author.setViolated(true);
				logger.error("Received unexpected move {} from {}: "
						+ move.toString(), data, author);
				throw new GameLogicException("Move was unexpected: "
						+ move.toString());
			}

			if (!board.isValideMove(move)) {
				author.setViolated(true);
				logger.error("Received invalid move {} from {}: "
						+ move.toString(), data, author);
				throw new GameLogicException("Move was invalid: "
						+ move.toString());
			}

			board.performMove(move);
			// author.addToHistory(move);
			next();
		} else {
			logger.error("Received unexpected {} from {}.", data, author);
			throw new GameLogicException("Unknown ObjectType received.");
		}
	}

	@Override
	public IPlayer onPlayerJoined() throws TooManyPlayersException {
		if (this.players.size() >= GamePlugin.MAX_PLAYER_COUNT)
			throw new TooManyPlayersException();

		final Player player = new Player(this.availableColors.remove(0));
		this.board.addPlayer(player);
		this.players.add(player);

		return player;
	}

	@Override
	protected void next() {
		// final Player activePlayer = getActivePlayer();
		// Move lastMove = activePlayer.getLastMove();
		int activePlayerId = this.players.indexOf(this.activePlayer);
		activePlayerId = (activePlayerId + 1) % this.players.size();
		final Player nextPlayer = this.players.get(activePlayerId);
		next(nextPlayer);
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
				score.setValueAt(0, new BigDecimal(+1));
			}
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

		return p.hasViolated() ? new PlayerScore(ScoreCause.RULE_VIOLATION, 0)
				: new PlayerScore(ScoreCause.REGULAR, board.getGameStats(p
						.getPlayerColor())[5]);

	}

	@Override
	protected ActionTimeout getTimeoutFor(Player player) {
		return new ActionTimeout(true, 10000l, 2000l);
	}

	@Override
	protected boolean checkGameOverCondition() {
		return getTurn() >= GamePlugin.MAX_TURN_COUNT
				|| board.getSheeps(PlayerColor.PLAYER1).size() == 0
				|| board.getSheeps(PlayerColor.PLAYER2).size() == 0;
	}

	@Override
	public void loadFromFile(String file) {
		GameLoader gl = new GameLoader(new Class<?>[] { GameState.class,
				Board.class });
		Object gameInfo = gl.loadGame(Configuration.getXStream(), file);
		if (gameInfo != null) {
			loadGameInfo(gameInfo);
		}
	}

	@Override
	public void loadGameInfo(Object gameInfo) {
		if (gameInfo instanceof GameState) {
			this.board = ((GameState) gameInfo).getGame().getBoard();
		}
		if (gameInfo instanceof Board) {
			this.board = (Board) gameInfo;
		}
	}
}
