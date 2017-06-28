package sc.server.gaming;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.exceptions.RescuableClientException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.api.plugins.host.IGameListener;
import sc.framework.plugins.RoundBasedGameInstance;
import sc.framework.plugins.SimplePlayer;
import sc.protocol.responses.GamePausedEvent;
import sc.protocol.responses.JoinGameResponse;
import sc.protocol.responses.LeftGameEvent;
import sc.protocol.responses.MementoPacket;
import sc.protocol.responses.ObservationResponse;
import sc.protocol.responses.RoomPacket;
import sc.server.network.Client;
import sc.server.network.DummyClient;
import sc.server.network.IClient;
import sc.server.plugins.GamePluginInstance;
import sc.shared.GameResult;
import sc.shared.PlayerScore;
import sc.shared.ScoreCause;
import sc.shared.ScoreDefinition;
import sc.shared.SlotDescriptor;

/**
 * A wrapper for an actual <code>GameInstance</code>. GameInstances are provided
 * by the plugins. Additional mapping data (Client2Player) will be stored here.
 */
public class GameRoom implements IGameListener
{
	private static final Logger			  logger		= LoggerFactory
															        .getLogger(GameRoom.class);
	private final String				      id;
	private final GameRoomManager		  gameRoomManager;
	private final GamePluginInstance	provider;
	private final IGameInstance			  game;
	private List<ObserverRole>			  observers	= new LinkedList<ObserverRole>();
	private List<PlayerSlot>			    playerSlots	= new ArrayList<PlayerSlot>(2);
	private final boolean				      prepared;
	private GameStatus					      status		= GameStatus.CREATED;
	private GameResult					      result		= null;
	private boolean						        paused		= false;
	private final short               maxPlayerCount = 2;

	public enum GameStatus
	{
		CREATED, ACTIVE, OVER
	}

	public GameRoom(String id, GameRoomManager gameRoomManager,
			GamePluginInstance provider, IGameInstance game, boolean prepared)
	{
		if (provider == null)
		{
			throw new IllegalArgumentException("Provider must not be null");
		}

		this.id = id;
		this.provider = provider;
		this.game = game;
		this.prepared = prepared;
		this.gameRoomManager = gameRoomManager;
		this.playerSlots = new ArrayList<>(2);
		game.addGameListener(this);
	}

	public GamePluginInstance getProvider()
	{
		return this.provider;
	}

	public IGameInstance getGame()
	{
		return this.game;
	}

	/**
	 * Generate Game Result, set status to OVER and remove from gameRoomManager
	 * @param results
	 */
	@Override
	public synchronized void onGameOver(Map<SimplePlayer, PlayerScore> results)
	{
		if (isOver())
		{
			logger
					.warn("Game was already over but received another GameOver-Event.");
			return;
		}

		setStatus(GameStatus.OVER);
		this.result = generateGameResult(results);
		logger.info("The game {} is over. (regular={})", getId(), this.result
				.isRegular());
		broadcast(this.result);
		kickAllClients();
		this.game.destroy();
		this.gameRoomManager.remove(this);
	}


	/**
	 * Set ScoreDefinition and create GameResult Object from results parameter
	 * @param results map of Player and PlayerScore
	 * @return GameResult, containing all PlayerScores and
	 */
	private GameResult generateGameResult(Map<SimplePlayer, PlayerScore> results)
	{
		ScoreDefinition definition = getProvider().getPlugin().getScoreDefinition();
		List<PlayerScore> scores = new LinkedList<>();

		// restore order
		for (PlayerRole player : getPlayers())
		{
			PlayerScore score = results.get(player.getPlayer());

			if (score == null)
			{
				throw new RuntimeException("GameScore was not complete!");
			}

			// FIXME: remove cause != unknown
			if (score.getCause() != ScoreCause.UNKNOWN && !score.matches(definition))
			{
				throw new RuntimeException("ScoreSize did not match Definition");
			}

			scores.add(score);
		}
		return new GameResult(definition, scores, this.game.getWinners());
	}

	/**
	 * Send Object o to all Player in this room
	 * @param o Object containing the message
	 */
	private void broadcast(Object o)
	{
		broadcast(o, true);
	}

	/**
	 * Send Object o to all Players or all Players in this room
	 * @param o
	 * @param roomSpecific
	 */
	private void broadcast(Object o, boolean roomSpecific)
	{
		Object toSend = o;

		// If message is specific to room, wrap the message in a RoomPacket
		if (roomSpecific)
		{
			toSend = new RoomPacket(getId(), o);
		}

		// Send to all Players
		for (PlayerRole player : getPlayers())
		{
			logger.debug("sending {} to {}", o.getClass().getSimpleName(),
					player.getClient().getClass().getSimpleName());
			player.getClient().send(toSend);
		}

		// Send to all Observer
		observerBroadcast(toSend);
	}

	/**
	 * Send Message to all registered Observers
	 * @param toSend Message to send
	 */
	private void observerBroadcast(Object toSend)
	{
		for (ObserverRole observer : Collections.unmodifiableCollection(this.observers))
		{
			logger.debug("sending {} to observer {}",
					toSend.getClass().getSimpleName(),
					observer.getClient().getClass().getSimpleName());
			observer.getClient().send(toSend);
		}
	}

	/**
	 * Send {@link GameRoom#broadcast(Object,boolean) broadcast} message with {@link LeftGameEvent LeftGameEvent}
	 */
	private void kickAllClients()
	{
		logger.debug("Kicking clients (and observer)");
		broadcast(new LeftGameEvent(getId()), false);
	}

	/**
	 * send StateObject to all players and observers
	 * @param data State Object that derives Object
	 */
	@Override
	public void onStateChanged(Object data)
	{
		sendStateToObservers(data);
		sendStateToPlayers(data);
	}


  /**
   * {@link GameRoom#broadcast(Object,boolean) Broadcast} the error package to this room
   * @param errorPacket
   */
	public void onClientError(Object errorPacket) {
		// packet = createRoomPacket(errorPacket);
		broadcast(errorPacket, true);
	}

  /**
   * Sends the given Object to all Players
   * @param data
   */
	private void sendStateToPlayers(Object data)
	{
		for (PlayerRole player : getPlayers())
		{
			RoomPacket packet = createRoomPacket(new MementoPacket(data, player
					.getPlayer()));
			player.getClient().sendAsynchronous(packet);
		}
	}


	/**
	 * Sends the given Object to all Observers
	 * @param data
	 */
	private void sendStateToObservers(Object data)
	{
		RoomPacket packet = createRoomPacket(new MementoPacket(data, null));

		for (ObserverRole observer : this.observers)
		{
			logger.debug("sending state to observer {}", observer.getClient());
			observer.getClient().sendAsynchronous(packet);
		}
	}

	/**
	 * Create {@link RoomPacket RoomPacket} from id and data Object.
	 * @param data to be send
	 * @return prepared RoomPacket
	 */
	public RoomPacket createRoomPacket(Object data)
	{
		return new RoomPacket(getId(), data);
	}

	/**
	 * Getter for Room ID
	 * @return id of Room as String
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 * Let a client join a GameRoom. Starts a game, if all players joined.
	 * @param client
	 * @return
	 * @throws RescuableClientException
	 */
	public synchronized boolean join(Client client)
			throws RescuableClientException
	{
		PlayerSlot openSlot = null;

		for (PlayerSlot slot : this.playerSlots)
		{
			// find PlayerSlot that it not in use for the new Client
			if (slot.isEmpty() && !slot.isReserved())
			{
				openSlot = slot;
				break;
			}
		}

		// set GameRoom of new Slot, if at least one slot is open
		if (this.playerSlots.size() < getMaximumPlayerCount())
		{
			openSlot = new PlayerSlot(this);
			this.playerSlots.add(openSlot);
		}

		if (openSlot == null)
		{
			return false;
		}

		fillSlot(openSlot, client);

		return true;
	}

	/**
	 * If game is not prepared set attributes of PlayerSlot and start game if game is {@link #isReady() ready}
	 * @param openSlot
	 * @param client
	 * @throws RescuableClientException
	 */
	synchronized void fillSlot(PlayerSlot openSlot, Client client)
			throws RescuableClientException
	{
		openSlot.setClient(client); // set role of Slot as PlayerRole

		if (!isPrepared()) // is set when game is game is created or prepared
		{
			logger.debug("GameRoom was not prepared, syncSlots");
			// seems to happen every time a client manually connects to server (JoinRoomRequest)
			syncSlot(openSlot);
		}

		startIfReady();
	}

	/**
	 * sets player in GameState and sets player specific values (displayName, shoudbePaused, canTimeout).
	 * Registers player to role in given slot
	 * sends JoinGameResponse when successful
	 * @param slot
	 * @throws RescuableClientException
	 */
	private void syncSlot(PlayerSlot slot) throws RescuableClientException
	{
		SimplePlayer player = getGame().onPlayerJoined(); // make new player in gameState of game
		// set attributes for player XXX check whether this is needed for prepared games
		player.setDisplayName(slot.getDescriptor().getDisplayName());
		player.setShouldBePaused(slot.getDescriptor().isShouldBePaused());
		player.setCanTimeout(slot.getDescriptor().isCanTimeout());

		if (slot.isEmpty()) // needed for forced step, if client crashes before joining room
		{
			logger.warn("PlayerSlot is empty! Was this  Caused by a forced STEP?");
			slot.setClient(new DummyClient());
		}

		slot.setPlayer(player); // set player in role of slot
		slot.getClient().send(new JoinGameResponse(getId()));
	}

	/**
	 * Returns true, if game was prepared and all slots are in use or maxplayercount of game 
	 * (or any new attribute for readiness) is reached
	 * @return
	 */
	private boolean isReady()
	{
		if (isPrepared())
		{
			for (PlayerSlot slot : this.playerSlots)
			{
				if (slot.isEmpty())
				{
					return false;
				}
			}

			return true;
		}
		else
		{
			return this.playerSlots.size() == 2;
		}
	}

	/**
	 * Starts game, if gameStatus isn't over or
	 * @throws RescuableClientException
	 */
	private void startIfReady() throws RescuableClientException
	{
		logger.debug("startIfReady called");
		if (isOver())
		{
			logger.warn("Game is already over.");
			return;
		}

		if (!isReady())
		{
			// normally called, when only the first player has connected
			logger.info("Game isn't ready yet.");
			return;
		}

		start();
	}

	/**
	 * If the Game is prepared, sync all slots
	 * @throws RescuableClientException
	 */
	private void start() throws RescuableClientException
	{
		if (isPrepared()) // sync slots for prepared game. This was already called for PlayerSlots in a game created by a join
		{
			for (PlayerSlot slot : this.playerSlots)
			{
				// creates players in gameState and sets their attributes
				syncSlot(slot);
			}
		}

		setStatus(GameStatus.ACTIVE);

		this.game.start();

		logger.info("Started the game.");
	}

  /**
   * Get the number of players allowed in the game
   * @return number of allowed players
   */
	private int getMaximumPlayerCount()
	{
	  return maxPlayerCount;
	}

	/**
	 * Returns the list of slots (correct ordering).
	 *
	 * @return
	 */
	public List<PlayerSlot> getSlots()
	{
		return Collections.unmodifiableList(this.playerSlots);
	}

	/**
	 * Threadsafe method to Reserve Slots for the player
	 * @return a List of unique IDs
	 */
	public synchronized List<String> reserveAllSlots()
	{
		List<String> result = new ArrayList<>(this.playerSlots.size());

		for (PlayerSlot playerSlot : this.playerSlots)
		{
			result.add(playerSlot.reserve());
		}

		return result;
	}

	/**
	 * Received new move from player and execute move in game
	 * @param source
	 * @param data
	 * @throws RescuableClientException
	 */
	public synchronized void onEvent(Client source, Object data)
			throws RescuableClientException
	{
		if (isOver())
		{
			throw new RescuableClientException(
					"Game is already over, but got data: " + data.getClass());
		}

		this.game.onAction(resolvePlayer(source), data);
	}

	/**
	 * Getter for player out of all playerRoles
	 * @param source
	 * @return SimplePlayer instance
	 * @throws RescuableClientException
	 */
	private SimplePlayer resolvePlayer(Client source)
			throws RescuableClientException {
    for (PlayerRole role : getPlayers()) {
      if (role.getClient().equals(source)) {
        SimplePlayer resolvedPlayer = role.getPlayer();

        if (resolvedPlayer == null) {
          throw new RescuableClientException(
                  "Game isn't ready. Please wait before sending messages.");
        }

        return resolvedPlayer;
      }
    }

    throw new RescuableClientException("Client is not a member of game "
            + this.id);
  }

  /**
   * Get {@link PlayerRole Players} that occupy a slot
   * @return List of PlayerRole Objects
   */
	private Collection<PlayerRole> getPlayers()
	{
		LinkedList<PlayerRole> clients = new LinkedList<PlayerRole>();
		for (PlayerSlot slot : this.playerSlots)
		{
		  if (!slot.isEmpty()){
			  clients.add(slot.getRole());
		  }
		}
		return clients;
	}

  /**
   * Get Server {@link IClient Clients} of all {@link PlayerRole Players}
   * @return
   */
	public Collection<IClient> getClients()
	{
		LinkedList<IClient> clients = new LinkedList<IClient>();
		for (PlayerRole slot : getPlayers())
		{
			clients.add(slot.getClient());
		}
		return clients;
	}

  /**
   * Add a Server {@link Client Client} in the role of an Observer
   * @param source Client to be added
   */
	public void addObserver(Client source)
	{
		ObserverRole role = new ObserverRole(source, this);
		source.addRole(role);
		this.observers.add(role);
		source.send(new ObservationResponse(getId()));
	}

  /**
   * Pause or un-pause a game
   * @param pause true if game is to be paused
   */
	public synchronized void pause(boolean pause)
	{
		if (isOver())
		{
			logger.warn("Game is already over and can't be paused.");
		}

		// Unnecessary Pause event
		if (pause == isPaused())
		{
			logger.warn("Dropped unnecessary PAUSE toggle from {} to {}.",
					isPaused(), pause);
			return;
		}

		logger.info("Switching PAUSE from {} to {}.", isPaused(), pause);
		this.paused = pause;
		RoundBasedGameInstance<SimplePlayer> pausableGame = (RoundBasedGameInstance<SimplePlayer>) this.game; // XXX
		pausableGame.setPauseMode(isPaused());

		// continue execution
		if (!isPaused())
		{
			pausableGame.afterPause();
		}
	}

	/**
	 *
	 * @param forced
	 *            If true, game will be started even if there are not enough
	 *            players to complete the game. This should result in a
	 *            GameOver.
	 * @throws RescuableClientException
	 */
	public synchronized void step(boolean forced)
			throws RescuableClientException
	{
		if (this.status == GameStatus.CREATED)
		{
			if (forced)
			{
				logger.warn("Forcing a game to start.");
				start();
			}
			else
			{
				logger.warn("Game isn't active yet, step was not forced.");
			}

			return;
		}
		if (isPaused())
		{
			logger.info("Stepping.");
			((RoundBasedGameInstance<SimplePlayer>) this.game).afterPause(); // XXX
		}
		else
		{
			logger.warn("Can't step if the game is not paused.");
		}
	}

  /**
   * Kick all Player and destroy game afterwards
   */
	public void cancel()
	{
    if (!isOver())
    {
      kickAllClients();
      this.game.destroy();
    }
	}

  /**
   * Broadcast to all observers, that the game is paused
   * @param nextPlayer Player to do the next move
   */
	@Override
	public void onPaused(SimplePlayer nextPlayer)
	{
		observerBroadcast(new RoomPacket(getId(), new GamePausedEvent(nextPlayer)));
	}

	/**
	 * Set descriptors of PlayerSlots
	 * @param descriptors to be set
	 * @throws TooManyPlayersException
	 */
	public void openSlots(List<SlotDescriptor> descriptors)
			throws TooManyPlayersException
	{
		if (descriptors.size() > 2){
		  throw new TooManyPlayersException();
    }

    // Can be 0 or 1
		for (int i = 0; i < descriptors.size(); i++)
		{
			this.playerSlots.get(i).setDescriptor(descriptors.get(i));
		}
	}

	/**
	 * If game is prepared return true
	 * @return true if Game is prepared
	 */
	public boolean isPrepared()
	{
		return this.prepared;
	}

	/**
	 * Return true if GameStatus is OVER
	 * @return true if Game is over
	 */
	public boolean isOver()
	{
		return getStatus() == GameStatus.OVER;
	}

	/**
	 * Return true if game is paused
	 * @return true, if game is paused
	 */
	public boolean isPaused()
	{
		return this.paused;
	}

	/**
	 * Get the current status of the Game
	 * @return
	 */
	public GameStatus getStatus()
	{
		return this.status;
	}

  /**
   * Update the {@link GameStatus status} of current Game
   * @param status status to be set
   */
	protected void setStatus(GameStatus status)
	{
		logger.info("Updating Status to {} (was: {})", status, getStatus());
		this.status = status;
	}

  /**
   * Remove specific player by calling {@link IGameInstance#onPlayerLeft(SimplePlayer) onPlayerLeft(player)}
   * @param player to be removed
   */
	public void removePlayer(SimplePlayer player)
	{
		logger.info("Removing {} from {}", player, this);
		this.game.onPlayerLeft(player);
	}

  /**
   * Get the saved {@link GameResult result}
   * @return GameResult Object
   */
	public GameResult getResult()
	{
		return this.result;
	}
}
