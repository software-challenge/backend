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
import sc.protocol.requests.PrepareGameRequest;
import sc.protocol.responses.PrepareGameResponse;
import sc.server.Configuration;
import sc.server.ServiceManager;
import sc.server.network.Client;
import sc.server.plugins.GamePluginInstance;
import sc.server.plugins.GamePluginManager;
import sc.server.plugins.UnknownGameTypeException;
import sc.shared.SlotDescriptor;

/**
 * The GameManager is responsible to keep all games alive and kill them once
 * they are done. Additionally the GameManger has to detect and kill games,
 * which seem to be dead-locked or have caused a timeout.
 *
 * @author mja
 * @author rra
 */
public class GameRoomManager
{
	private static Logger			logger				= LoggerFactory
																.getLogger(GameRoomManager.class);
	private Map<String, GameRoom>	rooms				= new HashMap<>();
	private final GamePluginManager	gamePluginManager	= new GamePluginManager();
	private GamePluginApi			pluginApi			= new GamePluginApi();

	public GameRoomManager()
	{
		this.gamePluginManager.reload();
		this.gamePluginManager.activateAllPlugins(this.pluginApi);
	}

	/**
	 * Adds an active game to the <code>GameManager</code>
	 *
	 * @param room
	 */
	private void add(GameRoom room)
	{
		logger.debug("Adding room with id {}", room.getId());
		this.rooms.put(room.getId(), room);
	}

	/**
	 * Create a not prepared GameRoom of given type
	 * @param gameType
	 * @return
	 * @throws RescueableClientException
	 */
	public synchronized GameRoom createGame(String gameType)
			throws RescueableClientException
	{
		return createGame(gameType, false);
	}

	/**
	 * make new PluginManager, generate roomId, create Game and GameRoom. If gameFile is set, load gameState from file
	 * @param gameType
	 * @param prepared signals whether the game was prepared by gui or ..., false if player has to send JoinRoomRequest
	 * @return newly created GameRoom
	 * @throws RescueableClientException
	 */
	public synchronized GameRoom createGame(String gameType, boolean prepared)
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
		GameRoom room = new GameRoom(roomId, this, plugin, plugin.createGame(),
				prepared);

		String gameFile = Configuration.get("loadGameFile");
		if (gameFile != null && gameFile != "") {
			logger.info("Request plugin to load game from file: " + gameFile);
			room.getGame().loadFromFile(gameFile);
		}

		this.add(room);

		return room;
	}

	private static synchronized  String generateRoomId()
	{
		return UUID.randomUUID().toString();
	}

	/**
	 * Open new GameRoom and join Client
	 * @param client
	 * @param gameType
	 * @return
	 * @throws RescueableClientException
	 */
	public synchronized boolean createAndJoinGame(Client client, String gameType)
			throws RescueableClientException
	{
		GameRoom room = createGame(gameType);
		return room.join(client);
	}

	/**
	 * Called after JoinRoomRequest. Client joins already existing GameRoom or opens new one
	 * @param client
	 * @param gameType
	 * @return
	 * @throws RescueableClientException
	 */
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

	/**
	 * Let a client join the first GameRoom with the right id
	 * @param client
	 * @param id id of GameRoom e.g. 7dc299b1-dcd5-4854-8a02-90510754b943
	 * @return true if join was successful
	 * @throws RescueableClientException
	 */
	public synchronized boolean joinGame(Client client, String id)
			throws RescueableClientException
	{
		for (GameRoom game : getGames())
		{
			logger.debug("GameRoom has id: {}", game.getId());
			if (game.getId().equals(id))
			{
				return game.join(client);
			}
		}

		return false;
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

	/**
	 * Creates a new GameRoom {@link #createGame(String) createGame}, set descriptors of PlayerSlots, 
	 * if exists load state of game from file
	 * @param gameType
	 * @param playerCount
	 * @param descriptors dispalyName, canTimout and shouldBePaused
	 * @param loadGameInfo
	 * @return new PrepareGameResponse with roomId and slots
	 * @throws RescueableClientException
	 */
	public synchronized PrepareGameResponse prepareGame(String gameType,
			int playerCount, List<SlotDescriptor> descriptors, Object loadGameInfo)
			throws RescueableClientException {
		GameRoom room = createGame(gameType, true);
		room.openSlots(descriptors);

		if (loadGameInfo != null) {
			room.getGame().loadGameInfo(loadGameInfo);
		}

		return new PrepareGameResponse(room.getId(), room.reserveAllSlots());
	}

	public synchronized PrepareGameResponse prepareGame(String gameType,
			int playerCount, List<SlotDescriptor> descriptors)
			throws RescueableClientException
	{
		return prepareGame(gameType, playerCount, descriptors, null);
	}

	/**
	 *  Calls {@link #prepareGame(String, int, List, Object) prepareGame}
	 * @param prepared
	 * @return
	 * @throws RescueableClientException
	 */
	public synchronized PrepareGameResponse prepareGame(PrepareGameRequest prepared) throws RescueableClientException {
		return prepareGame(
				prepared.getGameType(), prepared.getPlayerCount(),
				prepared.getSlotDescriptors(), prepared.getLoadGameInfo());
	}

	/**
	 * Getter for GameRoom
	 * @param roomId
	 * @return returns GameRoom specified by rooId
	 * @throws RescueableClientException
	 */
	public synchronized GameRoom findRoom(String roomId)
			throws RescueableClientException
	{
		GameRoom room = this.rooms.get(roomId);

		if (room == null)
		{
			throw new RescueableClientException("Couldn't find a room with id " + roomId);
		}

		return room;
	}

	public void remove(GameRoom gameRoom)
	{
		this.rooms.remove(gameRoom.getId());
	}
}
