package sc.plugin2010;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.IPlayer;
import sc.api.plugins.exceptions.RescueableClientException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.api.plugins.host.IGameListener;
import sc.framework.plugins.RoundBasedGameInstance;
import sc.shared.PlayerScore;

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
 * 
 */
@XStreamAlias(value="hui:game")
public class Game extends RoundBasedGameInstance<Player>
{
	private static Logger		logger			= LoggerFactory
														.getLogger(Game.class);

	@XStreamOmitField
	private List<FigureColor>	availableColors	= new LinkedList<FigureColor>();
	
	@XStreamOmitField
	private boolean				oneLastMove		= false;
	
	private Board				board			= Board.create();

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
	protected boolean checkGameOverCondition()
	{
		boolean gameOver = getTurn() >= GamePlugin.MAX_TURN_COUNT;

		if (!oneLastMove)
			for (final Player p : this.players)
			{
				gameOver = gameOver || p.inGoal();
			}

		return gameOver;
	}

	@Override
	protected Object getCurrentState()
	{
		return new GameState(this);
	}

	@Override
	protected void onRoundBasedAction(IPlayer fromPlayer, Object data)
			throws RescueableClientException
	{
		final Player author = (Player) fromPlayer;
		if (data instanceof Move)
		{
			if (oneLastMove)
				oneLastMove = false;
			final Move move = (Move) data;

			if (board.isValid(move, author))
			{
				update(move, author);
				author.addToHistory(move);
			}
			else
			{
				HashMap<IPlayer, PlayerScore> res = new HashMap<IPlayer, PlayerScore>();
				for (final Player p : players)
					res.put(p, p.getScore());

				notifyOnGameOver(res);
			}

			for (final Player p : players)
			{
				p.setPosition(GameUtil
						.getGameResult(p, board.getOtherPlayer(p)));
			}

			next();
		}
		else
		{
			logger.warn("Ungültiger Zug von '{}'", author.getColor());
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
						{
							player.changeCarrotsAvailableBy(10);
						}
						else
						{
							player.changeCarrotsAvailableBy(30);
						}
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
						if (board.getTypeAt(player.getFieldNumber()).equals(FieldTyp.RABBIT))
							player.setMustPlayCard(true);
						break;
				}
				break;
			}
			case MOVE:
			case FALL_BACK:
			{
				if (board.getTypeAt(player.getFieldNumber()).equals(FieldTyp.RABBIT))
					player.setMustPlayCard(true);
				break;
			}
		}

		if (player.inGoal())
			oneLastMove = true;
	}

	@Override
	public IPlayer onPlayerJoined(int position) throws TooManyPlayersException
	{
		if (this.players.size() >= GamePlugin.MAX_PLAYER_COUNT)
			throw new TooManyPlayersException();

		final Player player = new Player(this.availableColors.remove(0));
		this.players.add(player);
		this.board.addPlayer(player);

		for (final IGameListener listener : this.listeners)
			listener.onPlayerJoined(player);

		return player;
	}

	@Override
	protected void next()
	{
		final Player active = getActivePlayer();
		Move last = active.getLastMove();
		int activePlayerId = this.players.indexOf(this.activePlayer);
		switch (board.getTypeAt(active.getFieldNumber()))
		{
			case RABBIT:
				switch (last.getTyp())
				{
					case MOVE:
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
		next(nextPlayer);
	}

	@Override
	public void onPlayerLeft(IPlayer player)
	{
		HashMap<IPlayer, PlayerScore> res = new HashMap<IPlayer, PlayerScore>();
		for (final Player p : players)
			res.put(p, p.getScore());

		players.remove(player);
		notifyOnGameOver(res);
	}

	@Override
	public boolean ready()
	{
		return players.size() == GamePlugin.MAX_PLAYER_COUNT;
	}

	@Override
	public void start()
	{
		for (final Player p : players)
		{
			p.notifyListeners(new WelcomeMessage(p.getColor()));
			p.setPosition(Position.TIE);
		}

		super.start();
	}

	@Override
	protected void onNewTurn()
	{
		final Player player = getActivePlayer();
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
	protected PlayerScore getScoreFor(Player p)
	{
		return p.getScore();
	}
}
