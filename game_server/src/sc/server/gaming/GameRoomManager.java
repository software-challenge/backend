package sc.server.gaming;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.TooManyPlayersException;
import sc.server.ServiceManager;
import sc.server.network.Client;
import sc.server.plugins.GamePluginInstance;
import sc.server.plugins.GamePluginManager;
import sc.server.plugins.UnknownGameTypeException;

/**
 * The GameManager is responsible to keep all games alive and kill them once
 * they are done. Additionally the GameManger has to detect and kill games,
 * which seem to be dead-locked or have caused a timeout.
 * 
 * @author mja
 * @author rra
 */
public class GameRoomManager implements Runnable
{
	private static Logger			logger				= LoggerFactory
																.getLogger(GameRoomManager.class);
	private Collection<GameRoom>	games				= new LinkedList<GameRoom>();
	private final GamePluginManager	gamePluginManager	= new GamePluginManager();
	private GamePluginApi			pluginApi			= new GamePluginApi();
	private Thread					serviceThread		= null;

	public GameRoomManager()
	{
		gamePluginManager.reload();
		gamePluginManager.activateAllPlugins(this.pluginApi);
	}

	/**
	 * Adds an active game to the <code>GameManager</code>
	 * 
	 * @param game
	 */
	private void add(GameRoom game)
	{
		this.games.add(game);
	}

	public GameRoom createGame(String gameType)
	{
		GamePluginInstance plugin = this.gamePluginManager.getPlugin(gameType);
		logger.info("Created new game of type " + gameType);

		GameRoom room = new GameRoom(plugin, plugin.createGame());

		this.add(room);

		return room;
	}

	public boolean createAndJoinGame(Client client, String gameType)
			throws UnknownGameTypeException
	{
		GameRoom room = createGame(gameType);
		return room.join(client);
	}

	public boolean joinOrCreateGame(Client client, String gameType)
			throws UnknownGameTypeException
	{
		for (GameRoom game : games)
		{
			if (game.join(client))
			{
				return true;
			}
		}

		return createAndJoinGame(client, gameType);
	}

	public boolean joinGame(Client client, int id)
	{
		for (GameRoom game : games)
		{
			if (game.getId() == id)
			{
				return game.join(client);
			}
		}

		return false;
	}

	public void start()
	{
		if (serviceThread == null)
		{
			serviceThread = ServiceManager.createService(this.getClass()
					.getSimpleName(), this);
			serviceThread.start();
		}
	}

	@Override
	public void run()
	{
		logger.info("GameRoomManager running.");

		// Nothing to do yet.
	}

	public void close()
	{
		if (serviceThread != null)
		{
			serviceThread.interrupt();
		}
	}

	public Collection<GameRoom> getGames()
	{
		return Collections.unmodifiableCollection(this.games);
	}

	public GamePluginManager getPluginManager()
	{
		return this.gamePluginManager;
	}

	public GamePluginApi getPluginApi()
	{
		return this.pluginApi;
	}

	public GamePreparationResponse prepareGame(String gameType, int playerCount)
			throws TooManyPlayersException
	{
		GameRoom room = createGame(gameType);
		room.setSize(playerCount);
		List<String> reservations = room.reserveAllSlots();
		
		return new GamePreparationResponse(reservations);
	}
}
