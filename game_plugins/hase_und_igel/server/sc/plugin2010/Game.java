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
import sc.api.plugins.host.PlayerScore;
import sc.framework.plugins.IPauseable;
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
public class Game extends SimpleGameInstance<Player> implements IPauseable
{
	private static final Logger	logger				= LoggerFactory
															.getLogger(Game.class);

	private Board				board;

	private int					activePlayerId;

	private List<FigureColor>	availableColors;

	private boolean				active;

	private int					turn;

	private int					actionsSinceFirstPlayerEnteredGoal;

	private boolean				paused;

	private boolean				moveRequested		= false;

	private Runnable			afterPauseAction	= null;
	private Object				afterPauseLock		= new Object();

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
		paused = false;
		actionsSinceFirstPlayerEnteredGoal = 0;
		activePlayerId = 0;
	}

	@Override
	public void onAction(IPlayer author, Object data) throws RescueableClientException
	{
		if (!active)
		{
			logger.debug("Game not active!");
			return;
		}

		logger.debug("Turn #{}: recv. data from '{}'", getTurn(),
				((Player) author).getColor());
		logger.debug("{}: {}", ((Player) author).getColor(), data);


		if (data instanceof Move)
		{
			if(!moveRequested)
			{
				throw new RescueableClientException("We didn't request a move from you yet.");
			}
			
			this.moveRequested = false;
			
			if (board.getTypeAt(players.get(0).getFieldNumber()).equals(FieldTyp.GOAL)
					|| board.getTypeAt(players.get(1).getFieldNumber()).equals(
							FieldTyp.GOAL))
			{
				actionsSinceFirstPlayerEnteredGoal++;
			}
			
			final Player player = players.get(activePlayerId);
			
			activePlayerId = (activePlayerId + 1) % players.size();
			
			if (activePlayerId == 0)
			{
				turn++;
			}
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
			
			final Player next = fetchNextPlayer();
			updatePlayer(next);

			if (gameOver())
			{
				HashMap<IPlayer, PlayerScore> res = new HashMap<IPlayer, PlayerScore>();
				for (final Player p : players)
				{
					res.put(p, p.getScore());
				}
				
				active = false;
				for(final IGameListener l: listeners)
				{
					l.onGameOver(res);
				}
				updateObservers();
			}
			else
			{
				updateObservers();

				requestMove(next);
			}
		}
		else
		{
			logger.error("Unknown message received from '{}': '{}'", ((Player)author)
					.getColor(), data.getClass().getName());
		}
	}

	private void requestMove(final Player next)
	{
		if (this.paused)
		{
			synchronized (this.afterPauseLock)
			{
				logger.debug("Setting AfterPauseAction");

				this.afterPauseAction = new Runnable() {
					@Override
					public void run()
					{
						moveRequested = true;
						next.requestMove();
					}
				};

				for (IGameListener listener : this.listeners)
				{
					listener.onPaused();
				}
			}
		}
		else
		{
			moveRequested = true;
			next.requestMove();
		}
	}

	private boolean gameOver()
	{
		boolean gameOver = false;

		if (turn > GamePlugin.MAX_TURN_COUNT)
		{
			gameOver = true;
		}

		if (actionsSinceFirstPlayerEnteredGoal >= 1)
		{
			gameOver = true;
		}

		return gameOver;
	}

	/**
	 * Aktualisierungen vor dem Beginn einer neuen Runde werden ausgeführt: -
	 * Positionsfelder werden ausgewertet -
	 * 
	 * @param player
	 */
	private void updatePlayer(Player player)
	{
		FieldTyp current = board.getTypeAt(player.getFieldNumber());
		if (board.isFirst(player))
		{
			player.setPosition(0);
		} else {
			player.setPosition(1);
		}
		switch (current)
		{
			case POSITION_1:
				if (board.isFirst(player))
				{
					player.changeCarrotsAvailableBy(10);
				}
				break;
			case POSITION_2:
				if (!board.isFirst(player))
				{
					player.changeCarrotsAvailableBy(20);
				}
				break;
		}
	}

	/**
	 * Berechnet den nächsten Spieler der an die Reihe kommt. Spieler die noch
	 * aus der letzten Runde aussetzen müssen, werden wieder freigeschaltet.
	 * Sollte ein Spieler keine Zugmöglichkeit haben, so wird er übersprungen.
	 * 
	 * @return
	 */
	private Player fetchNextPlayer()
	{
		Player next = players.get(activePlayerId);
		if (!GameUtil.canMove(next, board))
		{
			activePlayerId = (activePlayerId + 1) % players.size();
		}

		logger.debug("next player is '{}'", players.get(activePlayerId)
				.getColor());

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
				{
					player.changeCarrotsAvailableBy(10);
				}
				else
				{
					player.changeCarrotsAvailableBy(30);
				}
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
		{
			throw new TooManyPlayersException();
		}

		player = new Player(availableColors.remove(0));
		players.add(player);
		board.addPlayer(player);

		// Informiere alle Beobachter von dem neuen Spieler
		for (final IGameListener listener : listeners)
		{
			listener.onPlayerJoined(player);
		}

		logger.debug("New player joined '{}'", player.getColor());

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
		GameState currentState = new GameState(this);

		for (final IGameListener l : listeners)
		{
			l.onStateChanged(currentState);
		}
	}

	@Override
	public void start()
	{
		active = true;
		activePlayerId = 0;

		for (final Player p : players)
		{
			p.notifyListeners(new WelcomeMessage(p.getColor()));
		}

		// Initialisiere alle Spieler
		updateObservers();

		// Fordere vom ersten Spieler einen Zug an
		final Player current = players.get(activePlayerId);
		logger.debug("Active Player is '{}'", current.getColor());

		requestMove(current);
	}

	@Override
	public boolean ready()
	{
		return players.size() == GamePlugin.MAX_PLAYER_COUNT;
	}

	@Override
	public void afterPause()
	{
		synchronized (this.afterPauseLock)
		{
			if (this.afterPauseAction == null)
			{
				logger
						.error("AfterPauseAction was null. Might cause a deadlock.");
			}
			else
			{
				Runnable action = this.afterPauseAction;
				this.afterPauseAction = null;
				action.run();
			}
		}
	}

	@Override
	public void setPauseMode(boolean pause)
	{
		this.paused = pause;
	}
}
