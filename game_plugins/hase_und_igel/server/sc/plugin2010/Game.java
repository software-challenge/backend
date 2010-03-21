package sc.plugin2010;

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
import sc.framework.plugins.ActionTimeout;
import sc.framework.plugins.RoundBasedGameInstance;
import sc.shared.PlayerScore;
import sc.shared.ScoreCause;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Die Spiellogik von Hase- und Igel.
 * 
 * Die Spieler spielen in genau der Reihenfolge in der sie das Spiel betreten
 * haben.
 * 
 * @author rra
 * @since Jul 4, 2009
 */
@XStreamAlias(value = "hui:game")
public class Game extends RoundBasedGameInstance<Player>
{
	private static Logger			logger			= LoggerFactory
															.getLogger(Game.class);

	@XStreamOmitField
	private List<FigureColor>		availableColors	= new LinkedList<FigureColor>();

	@XStreamOmitField
	private boolean					oneLastMove		= false;

	private Board					board			= Board.create();

	// Zugzeit berechnung für Score
	protected transient long		time_start;

	protected transient BigInteger	sum_red			= BigInteger.ZERO;
	protected transient BigInteger	sum_blue		= BigInteger.ZERO;

	public static final int			WIN_SCORE		= 1;
	public static final int			LOSE_SCORE		= 0;
	public static final int			DRAW_SCORE		= 1;

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

	public boolean hasLastMove()
	{
		return oneLastMove;
	}

	@Override
	protected boolean checkGameOverCondition()
	{
		boolean gameOver = getTurn() >= GamePlugin.MAX_TURN_COUNT;

		if (!oneLastMove)
			for (final Player p : this.players)
				gameOver = gameOver || p.inGoal();

		return gameOver;
	}

	@Override
	protected Object getCurrentState()
	{
		return new GameState(this);
	}

	@Override
	protected void onRoundBasedAction(IPlayer fromPlayer, Object data)
			throws GameLogicException
	{
		final Player author = (Player) fromPlayer;

		if (data instanceof Move)
		{
			oneLastMove = false;
			final Move move = (Move) data;

			if (author.getColor().equals(FigureColor.BLUE))
				sum_blue = sum_blue.add(BigInteger.valueOf(System
						.currentTimeMillis()
						- time_start));
			else
				sum_red = sum_red.add(BigInteger.valueOf(System
						.currentTimeMillis()
						- time_start));

			if (!board.isValid(move, author))
			{
				logger.error("Received invalid move {} from {}.", data, author);
				throw new GameLogicException("Move was invalid");
			}

			update(move, author);
			author.addToHistory(move);

			for (final Player p : players)
			{
				p.setPosition(GameUtil
						.getGameResult(p, board.getOtherPlayer(p)));
			}

			next();
		}
		else
		{
			logger.error("Received unexpected {} from {}.", data, author);
			throw new GameLogicException("Unknown ObjectType received.");
		}
	}

	private void update(Move move, Player player)
	{
		switch (move.getTyp())
		{
			case TAKE_OR_DROP_CARROTS:
				player.changeCarrotsAvailableBy(move.getN());
				break;
			case EAT:
				player.eatSalad();
				if (board.isFirst(player))
					player.changeCarrotsAvailableBy(10);
				else
					player.changeCarrotsAvailableBy(30);
				break;
			case MOVE:
				player.setFieldNumber(player.getFieldNumber() + move.getN());
				player.changeCarrotsAvailableBy(-GameUtil.calculateCarrots(move
						.getN()));
				break;
			case FALL_BACK:
			{
				int nextField = board.getPreviousFieldByTyp(FieldTyp.HEDGEHOG,
						player.getFieldNumber());
				int diff = player.getFieldNumber() - nextField;
				player.changeCarrotsAvailableBy(diff * 10);
				player.setFieldNumber(nextField);
				break;
			}
			case PLAY_CARD:
			{
				final Action action = move.getCard();
				List<Action> remaining = player.getActions();
				remaining.remove(action);
				player.setActions(remaining);

				switch (action)
				{
					case EAT_SALAD:
						player.eatSalad();
						if (board.isFirst(player))
							player.changeCarrotsAvailableBy(10);
						else
							player.changeCarrotsAvailableBy(30);
						break;
					case FALL_BACK:
						if (board.isFirst(player))
							player.setFieldNumber(board.getOtherPlayer(player)
									.getFieldNumber() - 1);
						break;
					case HURRY_AHEAD:
						if (!board.isFirst(player))
							player.setFieldNumber(board.getOtherPlayer(player)
									.getFieldNumber() + 1);
						break;
					case TAKE_OR_DROP_CARROTS:
						player.changeCarrotsAvailableBy(move.getN());
						break;
				}
			}
			default:
				break;
		}

		player.setMustPlayCard(false);
		switch (move.getTyp())
		{
			case PLAY_CARD:
			{
				switch (move.getCard())
				{
					case FALL_BACK:
					case HURRY_AHEAD:
						if (board.getTypeAt(player.getFieldNumber()).equals(
								FieldTyp.RABBIT))
							player.setMustPlayCard(true);
						break;
				}
				break;
			}
			case MOVE:
			case FALL_BACK:
			{
				if (board.getTypeAt(player.getFieldNumber()).equals(
						FieldTyp.RABBIT))
					player.setMustPlayCard(true);
				break;
			}
		}

		if (player.inGoal() && !player.getColor().equals(FigureColor.BLUE))
			oneLastMove = true;
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
		switch (board.getTypeAt(activePlayer.getFieldNumber()))
		{
			case RABBIT:
				switch (lastMove.getTyp())
				{
					case MOVE:
						// Auf ein Hasenfeld gezogen: gleicher Spieler nochmal
						break;

					case PLAY_CARD:
						switch (lastMove.getCard())
						{
							case EAT_SALAD:
							case TAKE_OR_DROP_CARROTS:
								activePlayerId = (activePlayerId + 1)
										% this.players.size();
								break;
							case FALL_BACK:
							case HURRY_AHEAD:
								// Durch eine Hasenkarte auf ein Hasenfeld
								// gekommen:
								// gleicher Spieler nochmal
								break;
						}
						break;
					default:
						activePlayerId = (activePlayerId + 1)
								% this.players.size();
						break;
				}
				break;
			default:
				activePlayerId = (activePlayerId + 1) % this.players.size();
				break;
		}
		final Player nextPlayer = this.players.get(activePlayerId);
		onPlayerChange(nextPlayer);
		next(nextPlayer);
		time_start = System.currentTimeMillis();
	}

	private void onPlayerChange(Player player)
	{
		switch (board.getTypeAt(player.getFieldNumber()))
		{
			case POSITION_1:
				if (board.isFirst(player))
					player.changeCarrotsAvailableBy(10);
				break;
			case POSITION_2:
				if (!board.isFirst(player))
					player.changeCarrotsAvailableBy(30);
				break;
			default:
				break;
		}
	}

	@Override
	public void onPlayerLeft(IPlayer player)
	{
		Map<IPlayer, PlayerScore> res = generateScoreMap();

		for (Entry<IPlayer, PlayerScore> entry : res.entrySet())
		{
			PlayerScore score = entry.getValue();

			if (entry.getKey() == player)
			{
				score.setCause(ScoreCause.LEFT);
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
			p.setPosition(Position.TIE);
		}

		time_start = System.currentTimeMillis();
		super.start();
	}

	@Override
	protected void onNewTurn()
	{
	}

	@Override
	protected PlayerScore getScoreFor(Player p)
	{
		long avg_time = 0;
		if (p.getColor().equals(FigureColor.BLUE))
		{
			sum_blue = sum_blue.divide(BigInteger
					.valueOf(p.getHistory().size() > 0 ? p.getHistory().size()
							: 1));
			avg_time = sum_blue.longValue();
		}
		else
		{
			sum_red = sum_red.divide(BigInteger
					.valueOf(p.getHistory().size() > 0 ? p.getHistory().size()
							: 1));
			avg_time = sum_red.longValue();
		}
		return p.getScore((int) avg_time);
	}
	
	@Override
	protected ActionTimeout getTimeoutFor(Player player)
	{
		return new ActionTimeout(true, 10000l, 2000l);
	}
}
