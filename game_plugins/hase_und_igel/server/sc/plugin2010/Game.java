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

	private int					actionsSinceFirstPlayerEnteredGoal;

	public Game()
	{
		availableColors = new LinkedList<FigureColor>();
		initialize();
	}

	protected Board getBoard()
	{
		return board;
	}

	protected final int getTurn()
	{
		return turn;
	}

	protected final boolean isActive()
	{
		return active;
	}

	protected final Player getActivePlayer()
	{
		return players.get(activePlayerId);
	}

	protected void initialize()
	{
		availableColors.addAll(Arrays.asList(FigureColor.values()));

		board = Board.create();

		active = false;
		turn = 0;
		actionsSinceFirstPlayerEnteredGoal = 0;
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

		if (board.getTypeAt(players.get(0).getPosition()).equals(FieldTyp.GOAL) ||
				board.getTypeAt(players.get(1).getPosition()).equals(FieldTyp.GOAL))
			actionsSinceFirstPlayerEnteredGoal++;
		
		final Player player = players.get(activePlayerId);

		activePlayerId = (activePlayerId + 1) % players.size();

		if (activePlayerId == 0)
			turn++;

		if (data instanceof Move)
		{
			final Move move = (Move) data;
			move.setTurn(getTurn());

			if (board.isValid(move, player))
			{
				updateBoardWith(move, player);
			}
			else
			{
				logger.error("Received invalid move from '{}': '{}'", player
						.getColor(), move.getTyp());
				active = false;
			}
			player.addToHistory(move);

			Player next = fetchNextPlayer();
			updatePlayer(next);

			if (gameOver())
			{
				active = false;
			}

			updatePlayers();
			next.requestMove();
			updateObservers();
		}
		else
		{
			logger.error("Unknown message received from '{}': '{}'", player
					.getColor(), data.getClass().getName());
		}
	}

	private boolean gameOver()
	{
		boolean gameOver = false;

		if (turn > GamePlugin.MAX_TURN_COUNT)
			gameOver = true;

		if (actionsSinceFirstPlayerEnteredGoal >= 1)
			gameOver = true;

		return gameOver;
	}

	/**
	 * Aktualisierungen vor dem Beginn einer neuen Runde werden ausgeführt:
	 * - Positionsfelder werden ausgewertet
	 * -
	 * 
	 * @param player
	 */
	private void updatePlayer(Player player)
	{
		FieldTyp current = board.getTypeAt(player.getPosition());
		switch (current)
		{
			case POSITION_1:
				if (board.isFirst(player))
					player.changeCarrotsAvailableBy(10);
				break;
			case POSITION_2:
				if (!board.isFirst(player))
					player.changeCarrotsAvailableBy(20);
				break;
		}
	}

	/**
	 * Berechnet den nächsten Spieler der an die Reihe kommt. Spieler die noch
	 * aus
	 * der letzten Runde aussetzen müssen, werden wieder freigeschaltet.
	 * Sollte ein Spieler keine Zugmöglichkeit haben, so wird er übersprungen.
	 * 
	 * @return
	 */
	private Player fetchNextPlayer()
	{
		Player next = players.get(activePlayerId);
		if (!GameUtil.canMove(next, board))
			activePlayerId = (activePlayerId + 1) % players.size();

		return players.get(activePlayerId);
	}

	protected void updateBoardWith(Move move, Player player)
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
				player.setPosition(player.getPosition() + move.getN());
				player.changeCarrotsAvailableBy(-GameUtil.calculateCarrots(move
						.getN()));
				break;
			case FALL_BACK:
			{
				int nextField = board.getPreviousFieldByTyp(FieldTyp.HEDGEHOG,
						player.getPosition());
				int diff = player.getPosition() - nextField;
				player.changeCarrotsAvailableBy(diff * 10);
				player.setPosition(nextField);
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
					case FALL_BACK:
						player.setPosition(board.getOtherPlayer(player)
								.getPosition() - 1);
						break;
					case HURRY_AHEAD:
						player.setPosition(board.getOtherPlayer(player)
								.getPosition() + 1);
						break;
					case TAKE_OR_DROP_CARROTS:
						player.changeCarrotsAvailableBy(move.getN());
						break;
				}
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
