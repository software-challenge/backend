package sc.plugin_minimal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.IPlayer;
import sc.api.plugins.exceptions.GameLogicException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.api.plugins.host.GameLoader;
import sc.framework.plugins.ActionTimeout;
import sc.framework.plugins.RoundBasedGameInstance;
import sc.plugin_minimal.util.Configuration;
import sc.shared.PlayerScore;
import sc.shared.ScoreCause;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Minimal game. Basis for new plugins.
 * This class holds the game logic.
 * 
 * @author Sven Casimir
 * @since Juni, 2010
 */
@XStreamAlias(value = "minimal:game")
public class Game extends RoundBasedGameInstance<Player>
{
	private static Logger			logger			= LoggerFactory
															.getLogger(Game.class);

	@XStreamOmitField
	private List<FigureColor>		availableColors	= new LinkedList<FigureColor>();

	private Board					board			= Board.create();

	public Board getBoard()
	{
		return board;
	}

	public Player getActivePlayer()
	{
		return activePlayer;
	}

	public Game()
	{
		availableColors.addAll(Arrays.asList(FigureColor.values()));
	}

	@Override
	protected Object getCurrentState()
	{
		return new GameState(this);
	}

	/**
	 * Someone did something, check out what it was (move maybe? Then check the move)
	 */
	@Override
	protected void onRoundBasedAction(IPlayer fromPlayer, Object data)
			throws GameLogicException
	{
		final Player author = (Player) fromPlayer;

		// Player did a move
		if (data instanceof Move)
		{
			final Move move = (Move) data;

			update(move, author);
			author.addToHistory(move);
			next();
		}
		else
		{
			logger.error("Received unexpected {} from {}.", data, author);
			throw new GameLogicException("Unknown ObjectType received.");
		}
	}

	/**
	 * A move has been made, update local game state
	 * @param move
	 * @param player
	 */
	private void update(Move move, Player player)
	{
	
	}

	@Override
	public IPlayer onPlayerJoined() throws TooManyPlayersException
	{
		if (this.players.size() >= GamePlugin.MAX_PLAYER_COUNT)
			throw new TooManyPlayersException();

		final Player player = new Player(this.availableColors.remove(0));
		this.board.addPlayer(player);
		this.players.add(player);

		return player;
	}

	@Override
	protected void next()
	{
		final Player activePlayer = getActivePlayer();
		Move lastMove = activePlayer.getLastMove();
		int activePlayerId = this.players.indexOf(this.activePlayer);
		activePlayerId = (activePlayerId + 1) % this.players.size();
		final Player nextPlayer = this.players.get(activePlayerId);
		onPlayerChange(nextPlayer);
		next(nextPlayer);
	}

	private void onPlayerChange(Player player)
	{
	}
	
	@Override
	public void onPlayerLeft(IPlayer player) {
		if(!player.hasViolated()) {
			onPlayerLeft(player, ScoreCause.LEFT);
		} else {
			onPlayerLeft(player, ScoreCause.RULE_VIOLATION);
		}
	}

	@Override
	public void onPlayerLeft(IPlayer player, ScoreCause cause)
	{
		Map<IPlayer, PlayerScore> res = generateScoreMap();

		for (Entry<IPlayer, PlayerScore> entry : res.entrySet())
		{
			PlayerScore score = entry.getValue();

			if (entry.getKey() == player)
			{
				score.setCause(cause);
				score.setValueAt(0, new BigDecimal(0));
			}
			else
			{
				score.setValueAt(0, new BigDecimal(+1));
			}
		}

		notifyOnGameOver(res);
	}

	@Override
	public boolean ready()
	{
		return this.players.size() == GamePlugin.MAX_PLAYER_COUNT;
	}

	@Override
	public void start()
	{
		for (final Player p : players)
		{
			p.notifyListeners(new WelcomeMessage(p.getColor()));
		}

		super.start();
	}

	@Override
	protected void onNewTurn()
	{
	}

	@Override
	protected PlayerScore getScoreFor(Player p)
	{
		return null;
	}
	
	@Override
	protected ActionTimeout getTimeoutFor(Player player)
	{
		return new ActionTimeout(true, 10000l, 2000l);
	}

	@Override
	protected boolean checkGameOverCondition() {
		return getTurn() >= GamePlugin.MAX_TURN_COUNT;
	}

	@Override
	public void loadFromFile(String file) {
		GameLoader<GameState> gl = new GameLoader<GameState>(GameState.class);
		GameState state = gl.loadGame(Configuration.getXStream(), file);
		if (state != null) {
			this.board = state.getGame().getBoard();
		}
	}
}
