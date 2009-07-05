package sc.plugin2010;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGameListener;
import sc.api.plugins.IPlayer;
import sc.api.plugins.TooManyPlayersException;
import sc.networking.TcpNetwork;
import sc.plugin2010.Player.FigureColor;

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
public class Game implements IGameInstance
{
	private static final Logger	logger	= LoggerFactory.getLogger(Game.class);

	private Board				board;

	private List<Player>		players;

	private int					activePlayerId;

	private Set<IGameListener>	listeners;

	private List<FigureColor>	availableColors;

	private boolean				active;

	public Game()
	{
		players = new LinkedList<Player>();
		listeners = new HashSet<IGameListener>();

		availableColors = new LinkedList<FigureColor>();
		initialize();
	}

	private void initialize()
	{
		availableColors.addAll(Arrays.asList(FigureColor.values()));

		// TODO entgültige Länge der Rennstrecke bestimmen
		board = Board.create(20);

		active = false;
		activePlayerId = 0;
	}

	@Override
	public void actionReceived(IPlayer author, Object data)
	{
		if (data instanceof Move)
		{
			logger.info("Spieler '{}' hat einen Zug gemacht.", author);
			final Move move = (Move) data;

			// TODO den Zug des Spielers verarbeiten
			
			// TODO überprüfen, ob das Spiel vorbei ist
			// TODO Beobachter benachrichtigen
			
			// Nächsten Spieler benachrichtigen
			activePlayerId++;
			if (players.get(activePlayerId).isSuspended())
				activePlayerId++;
			
			activePlayerId = activePlayerId % players.size();
			
			players.get(activePlayerId).update(new MoveRequested());
		}
		else
		{
			logger.error("Unerwartet Antwort von Spieler '{}'", author);
		}
	}

	@Override
	public void addGameListener(IGameListener listener)
	{
		listeners.add(listener);
	}

	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public synchronized IPlayer playerJoined() throws TooManyPlayersException
	{
		Player player = null;
		if (players.size() >= GamePlugin.MAX_PLAYER_COUNT)
			throw new TooManyPlayersException();

		player = new Player(availableColors.remove(0));
		players.add(player);

		// Informiere alle Beobachter von dem neuen Spieler
		for (final IGameListener listener : listeners)
		{
			listener.onPlayerJoined(player);
		}

		return player;
	}

	@Override
	public void playerLeft(IPlayer player)
	{
		players.remove(player);

		for (final IGameListener listener : listeners)
		{
			listener.onPlayerLeft(player);
		}

		if (active)
		{
			active = false;
			for (final IGameListener listener : listeners)
			{
				// TODO das Endergebnis kann noch nicht übergeben werden.
				listener.onGameOver();
			}
		}
	}

	@Override
	public void removeGameListener(IGameListener listener)
	{
		listeners.remove(listener);
	}

	@Override
	public void start()
	{
		active = true;
		activePlayerId = 0;

		// Initialisiere alle Spieler
		for (final Player player : players)
		{
			player.update(new BoardUpdated(board));
			for (final Player other : players)
			{
				if (other.equals(player))
					continue;
				player.update(new PlayerUpdated(other, false));
			}

			player.update(new PlayerUpdated(player, true));
		}
		
		// Fordere vom ersten Spieler einen Zug an
		players.get(activePlayerId).update(new MoveRequested());
	}
}
