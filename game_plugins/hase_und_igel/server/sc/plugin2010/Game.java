package sc.plugin2010;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.IPlayer;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.api.plugins.host.IGameListener;
import sc.framework.plugins.SimpleGameInstance;
import sc.plugin2010.Board.FieldTyp;
import sc.plugin2010.Player.Action;
import sc.plugin2010.Player.FigureColor;
import sc.plugin2010.util.GameUtil;

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
public class Game extends SimpleGameInstance<Player>
{
	private static final Logger	logger	= LoggerFactory.getLogger(Game.class);

	private Board				board;

	private int					activePlayerId;

	private List<FigureColor>	availableColors;

	private boolean				active;

	private int					turn;

	public Game()
	{
		availableColors = new LinkedList<FigureColor>();
		initialize();
	}

	protected void initialize()
	{
		availableColors.addAll(Arrays.asList(FigureColor.values()));

		board = Board.create();

		active = false;
		turn = 0;
		activePlayerId = 0;
	}

	@Override
	public void onAction(IPlayer author, Object data)
	{
		if (!active)
		{
			logger.info("Game not active!");
			return;
		}

		final Player active = players.get(activePlayerId);
		activePlayerId = (activePlayerId + 1) % players.size();

		if (data instanceof Move)
		{
			logger.info("Player '{}' has made a move.", author);
			final Move move = (Move) data;

			if (board.isValid(move, active))
			{
				updateBoard(move, active);
			}
			else
			{
				logger.error("Received invalid move from '{}': '{}'", active
						.getColor(), move.getTyp());
			}

			if (!playerCanMove(players.get(activePlayerId)))
			{
				activePlayerId = (activePlayerId + 1) % players.size();
			}

			updatePlayers();
			players.get(activePlayerId).requestMove();
			updateObservers();
		}
		else
		{
			logger.error("Unknown message received from '{}': '{}'", active
					.getColor(), data.getClass().getName());
		}
	}

	protected boolean playerCanMove(Player next)
	{
		// TODO Auto-generated method stub
		return true;
	}

	protected void updateBoard(Move move, Player player)
	{
		switch (move.getTyp())
		{
			case DROP_10_CARROTS:
				player.setCarrotsAvailable(Math.max(0, player
						.getCarrotsAvailable()
						- move.getN()));
				break;
			case TAKE_10_CARROTS:
				player.setCarrotsAvailable(player.getCarrotsAvailable() + 10);
				break;
			case PLAY_CARD_EAT_SALAD:
			{
				List<Action> remaining = player.getActions();
				remaining.remove(Action.EAT_SALAD);
				player.setActions(remaining);
			}
			case EAT:
				player.setSaladsToEat(Math.max(0, player.getSaladsToEat() - 1));
				break;
			case MOVE:
				player.setPosition(player.getPosition() + move.getN());
				player.setCarrotsAvailable(player.getCarrotsAvailable()
						- GameUtil.calculateCarrots(move.getN()));
				break;
			case PLAY_CARD_FALL_BACK:
			{
				List<Action> remaining = player.getActions();
				remaining.remove(Action.FALL_BACK);
				player.setActions(remaining);
			}
			case FALL_BACK:
			{
				int nextField = board.getPreviousFieldByTyp(FieldTyp.HEDGEHOG,
						player.getPosition());
				int diff = player.getPosition() - nextField;
				// TODO increase carrot count
				player.setPosition(nextField);
				break;
			}
			case PLAY_CARD_HURRY_AHEAD:
			{
				List<Action> remaining = player.getActions();
				remaining.remove(Action.HURRY_AHEAD);
				player.setActions(remaining);

				int nextField = board.getOtherPlayer(player).getPosition() + 1;
				player.setPosition(nextField);
				break;
			}
			case PLAY_CARD_DROP_20_CARROTS:
			{
				List<Action> remaining = player.getActions();
				remaining.remove(Action.DROP_20_CARROTS);
				remaining.remove(Action.TAKE_20_CARROTS);
				player.setActions(remaining);

				player.setCarrotsAvailable(Math.max(0, player
						.getCarrotsAvailable()
						- move.getN()));
				break;
			}
			case PLAY_CARD_TAKE_20_CARROTS:
			{
				List<Action> remaining = player.getActions();
				remaining.remove(Action.DROP_20_CARROTS);
				remaining.remove(Action.TAKE_20_CARROTS);
				player.setActions(remaining);

				player.setCarrotsAvailable(player.getCarrotsAvailable()
						+ move.getN());
				break;
			}
			default:
				break;
		}
	}

	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public synchronized IPlayer onPlayerJoined() throws TooManyPlayersException
	{
		Player player = null;
		if (players.size() >= GamePlugin.MAX_PLAYER_COUNT)
			throw new TooManyPlayersException();

		player = new Player(availableColors.remove(0));
		players.add(player);
		board.addPlayer(player);

		// Informiere alle Beobachter von dem neuen Spieler
		for (final IGameListener listener : listeners)
		{
			listener.onPlayerJoined(player);
		}

		return player;
	}

	@Override
	public void onPlayerLeft(IPlayer player)
	{
		players.remove(player);

		for (final IGameListener listener : listeners)
		{
			listener.onPlayerLeft(player);
		}

		if (active)
		{
			active = false;
			notifyOnGameOver();
		}
	}

	private void updateObservers()
	{
		for (final IGameListener l : listeners)
		{
			// TODO get Momento
			l.onStateChanged(null);
		}
	}

	private void updatePlayers()
	{
		for (final Player player : players)
		{
			player.notifyListeners(new BoardUpdated(board, turn));
			for (final Player other : players)
			{
				if (other.equals(player))
					continue;
				player.notifyListeners(new PlayerUpdated(other, false));
			}

			player.notifyListeners(new PlayerUpdated(player, true));
		}
	}

	@Override
	public void start()
	{
		active = true;
		activePlayerId = 0;

		// Initialisiere alle Spieler
		updatePlayers();

		// Fordere vom ersten Spieler einen Zug an
		players.get(activePlayerId).requestMove();
	}

	@Override
	public boolean ready()
	{
		return this.players.size() == GamePlugin.MAX_PLAYER_COUNT;
	}
}
