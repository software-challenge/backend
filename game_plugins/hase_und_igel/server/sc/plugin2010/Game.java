package sc.plugin2010;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.IPlayer;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.api.plugins.host.IGameListener;
import sc.framework.plugins.SimpleGameInstance;
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
public class Game extends SimpleGameInstance<Player>
{
	private static final Logger	logger	= LoggerFactory.getLogger(Game.class);

	private Board				board;

	private int					activePlayerId;

	private List<FigureColor>	availableColors;

	private boolean				active;

	public Game()
	{
		availableColors = new LinkedList<FigureColor>();
		initialize();
	}

	private void initialize()
	{
		availableColors.addAll(Arrays.asList(FigureColor.values()));

		board = Board.create();

		active = false;
		activePlayerId = 0;
	}

	@Override
	public void onAction(IPlayer author, Object data)
	{
		if (!active)
			return;
		
		if (data instanceof Move)
		{
			logger.info("Spieler '{}' hat einen Zug gemacht.", author);
			final Move move = (Move) data;

			// TODO den Zug des Spielers verarbeiten
			
			// TODO überprüfen, ob das Spiel vorbei ist
			boolean gameOver = false;
			if (gameOver)
			{
				for(final Player player : players)
				{
					// TODO result spielerspezifisch umbauen
					player.notifyListeners(new GameOver(0));
				}
			} else 
			{
				// Aktuellen Spielstand übertragen
				updatePlayers();
				
				// Nächsten Spieler benachrichtigen
				activePlayerId = (activePlayerId + 1) % players.size();
				if (players.get(activePlayerId).isSuspended())
					activePlayerId = (activePlayerId + 1) % players.size();
				
				players.get(activePlayerId).requestMove();	
			}
			
			updateObservers();
		}
		else
		{
			logger.error("Unerwartet Antwort von Spieler '{}'", author);
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
		// TODO
	}
	
	private void updatePlayers()
	{
		for (final Player player : players)
		{
			player.notifyListeners(new BoardUpdated(board));
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
