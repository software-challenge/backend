package sc.server.gaming;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.exceptions.RescueableClientException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.protocol.responses.PrepareGameResponse;
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
	private Map<String, GameRoom>	rooms				= new HashMap<String, GameRoom>();
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
	 * @param room
	 */
	private void add(GameRoom room)
	{
		this.rooms.put(room.getId(), room);
	}

	public synchronized GameRoom createGame(String gameType)
			throws RescueableClientException
	{
		GamePluginInstance plugin = this.gamePluginManager.getPlugin(gameType);

		if (plugin == null)
		{
			logger.warn("Couldn't find a game of type " + gameType);
			throw new UnknownGameTypeException(gameType, this.gamePluginManager
					.getPluginUUIDs());
		}

		logger.info("Created new game of type " + gameType);

		String roomId = generateRoomId();
		GameRoom room = new GameRoom(roomId, plugin, plugin.createGame());

		this.add(room);

		return room;
	}

	private synchronized static String generateRoomId()
	{
		return UUID.randomUUID().toString();
	}

	public synchronized boolean createAndJoinGame(Client client, String gameType)
			throws RescueableClientException
	{
		GameRoom room = createGame(gameType);
		return room.join(client);
	}

	public synchronized boolean joinOrCreateGame(Client client, String gameType)
			throws RescueableClientException
	{
		for (GameRoom game : getGames())
		{
			if (game.join(client))
			{
				return true;
			}
		}

		return createAndJoinGame(client, gameType);
	}

	public synchronized boolean joinGame(Client client, String id)
	{
		for (GameRoom game : getGames())
		{
			if (game.getId().equals(id))
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

	public synchronized Collection<GameRoom> getGames()
	{
		return Collections.unmodifiableCollection(this.rooms.values());
	}

	public GamePluginManager getPluginManager()
	{
		return this.gamePluginManager;
	}

	public GamePluginApi getPluginApi()
	{
		return this.pluginApi;
	}

	public synchronized PrepareGameResponse prepareGame(String gameType, int playerCount)
			throws RescueableClientException
	{
		GameRoom room = createGame(gameType);
		room.setSize(playerCount);
		return new PrepareGameResponse(room.getId(), room.reserveAllSlots());
	}

	public synchronized GameRoom findRoom(String roomId) throws RescueableClientException
	{
		GameRoom room = this.rooms.get(roomId);

		if (room == null)
		{
			throw new RescueableClientException("Couldn't find a room with id "
					+ roomId);
		}

		return room;
	}
}
