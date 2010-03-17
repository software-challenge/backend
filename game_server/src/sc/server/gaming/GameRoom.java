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
import sc.api.plugins.IPlayer;
import sc.api.plugins.exceptions.RescueableClientException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.api.plugins.host.IGameListener;
import sc.framework.plugins.IPauseable;
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
	private static final Logger			logger		= LoggerFactory
															.getLogger(GameRoom.class);
	private final String				id;
	private final GameRoomManager		gameRoomManager;
	private final GamePluginInstance	provider;
	private final IGameInstance			game;
	private List<ObserverRole>			observers	= new LinkedList<ObserverRole>();
	private List<PlayerSlot>			playerSlots	= new ArrayList<PlayerSlot>(
															2);
	private final boolean				prepared;
	private GameStatus					status		= GameStatus.CREATED;
	private GameResult					result		= null;
	private boolean						paused		= false;

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

	@Override
	public synchronized void onGameOver(Map<IPlayer, PlayerScore> results)
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
		this.gameRoomManager.remove(this);
	}

	private GameResult generateGameResult(Map<IPlayer, PlayerScore> results)
	{
		ScoreDefinition definition = getProvider().getPlugin()
				.getScoreDefinition();
		List<PlayerScore> scores = new LinkedList<PlayerScore>();

		// restore order
		for (PlayerRole player : getPlayers())
		{
			PlayerScore score = results.get(player.getPlayer());

			if (score == null)
			{
				//throw new RuntimeException("GameScore was not complete!");
				
				// FIXME: hack to avoid server hangups
				// Gewinner, Feldnummer, Karotten, Zeit (ms)
				score = new PlayerScore(ScoreCause.UNKNOWN, 0, 0, 0, 0);
			}

			// FIXME: remove cause != unknown
			if (score.getCause() != ScoreCause.UNKNOWN && !score.matches(definition))
			{
				throw new RuntimeException("ScoreSize did not match Definition");
			}

			scores.add(score);
		}
		
		// FIXME: if there where not enough players, add scores
		while (scores.size() < 2)
		{
			scores.add(new PlayerScore(ScoreCause.UNKNOWN, 0, 0, 0, 0));
		}

		GameResult result = new GameResult(definition, scores);
		return result;
	}

	private void broadcast(Object o)
	{
		broadcast(o, true);
	}

	private void broadcast(Object o, boolean roomSpecific)
	{
		Object toSend = o;
		if (roomSpecific)
		{
			toSend = new RoomPacket(getId(), o);
		}

		for (PlayerRole player : getPlayers())
		{
			player.getClient().send(toSend);
		}

		observerBroadcast(toSend);
	}

	private void observerBroadcast(Object toSend)
	{
		for (ObserverRole observer : this.observers)
		{
			observer.getClient().send(toSend);
		}
	}

	private void kickAllClients()
	{
		broadcast(new LeftGameEvent(getId()), false);
	}

	@Override
	public void onStateChanged(Object data)
	{
		sendStateToObservers(data);
		sendStateToPlayers(data);
	}

	private void sendStateToPlayers(Object data)
	{
		for (PlayerRole player : getPlayers())
		{
			RoomPacket packet = createRoomPacket(new MementoPacket(data, player
					.getPlayer()));
			player.getClient().sendAsynchronous(packet);
		}
	}

	private void sendStateToObservers(Object data)
	{
		RoomPacket packet = createRoomPacket(new MementoPacket(data, null));

		for (ObserverRole observer : this.observers)
		{
			observer.getClient().sendAsynchronous(packet);
		}
	}

	public RoomPacket createRoomPacket(Object data)
	{
		return new RoomPacket(this.getId(), data);
	}

	public String getId()
	{
		return this.id;
	}

	public synchronized boolean join(Client client)
			throws RescueableClientException
	{
		PlayerSlot openSlot = null;

		for (PlayerSlot slot : this.playerSlots)
		{
			if (slot.isEmpty() && !slot.isReserved())
			{
				openSlot = slot;
				break;
			}
		}

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

	private synchronized void fillSlot(PlayerSlot openSlot, Client client)
			throws RescueableClientException
	{
		openSlot.setClient(client);

		if (!isPrepared())
		{
			syncSlot(openSlot);
		}

		startIfReady();
	}

	private void syncSlot(PlayerSlot slot) throws RescueableClientException
	{
		IPlayer player = getGame().onPlayerJoined();
		player.setDisplayName(slot.getDescriptor().getDisplayName());
		player.setShouldBePaused(slot.getDescriptor().isShouldBePaused());
		player.setCanTimeout(slot.getDescriptor().isCanTimeout());

		if (slot.isEmpty())
		{
			logger.warn("PlayerSlot is empty! Was this  Caused by a forced STEP?");
			slot.setClient(new DummyClient());
		}

		slot.setPlayer(player);
		slot.getClient().send(new JoinGameResponse(getId()));
	}

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
			return this.game.ready();
		}
	}

	private void startIfReady() throws RescueableClientException
	{
		if (isOver())
		{
			logger.warn("Game is already over.");
			return;
		}

		if (!isReady())
		{
			logger.info("Game isn't ready yet.");
			return;
		}

		start();
	}

	private void start() throws RescueableClientException
	{
		if (isPrepared())
		{
			for (PlayerSlot slot : this.playerSlots)
			{
				syncSlot(slot);
			}
		}

		setStatus(GameStatus.ACTIVE);

		this.game.start();

		logger.info("Started the game.");
	}

	private int getMaximumPlayerCount()
	{
		return this.provider.getPlugin().getMaximumPlayerCount();
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

	public synchronized void setSize(int playerCount)
			throws TooManyPlayersException
	{
		if (playerCount > getMaximumPlayerCount())
		{
			throw new TooManyPlayersException();
		}

		while (this.playerSlots.size() < playerCount)
		{
			this.playerSlots.add(new PlayerSlot(this));
		}
	}

	public synchronized List<String> reserveAllSlots()
	{
		List<String> result = new ArrayList<String>(this.playerSlots.size());

		for (PlayerSlot playerSlot : this.playerSlots)
		{
			result.add(playerSlot.reserve());
		}

		return result;
	}

	public synchronized void onEvent(Client source, Object data)
			throws RescueableClientException
	{
		if (isOver())
		{
			throw new RescueableClientException(
					"Game is already over, but got data: " + data.getClass());
		}

		this.game.onAction(resolvePlayer(source), data);
	}

	private IPlayer resolvePlayer(Client source)
			throws RescueableClientException
	{
		for (PlayerRole role : getPlayers())
		{
			if (role.getClient().equals(source))
			{
				IPlayer resolvedPlayer = role.getPlayer();

				if (resolvedPlayer == null)
				{
					throw new RescueableClientException(
							"Game isn't ready. Please wait before sending messages.");
				}

				return resolvedPlayer;
			}
		}

		throw new RescueableClientException("Client is not a member of game "
				+ this.id);
	}

	private Collection<PlayerSlot> getOccupiedPlayerSlots()
	{
		LinkedList<PlayerSlot> occupiedSlots = new LinkedList<PlayerSlot>();

		for (PlayerSlot slot : this.playerSlots)
		{
			if (!slot.isEmpty())
			{
				occupiedSlots.add(slot);
			}
		}

		return occupiedSlots;
	}

	private Collection<PlayerRole> getPlayers()
	{
		LinkedList<PlayerRole> clients = new LinkedList<PlayerRole>();
		for (PlayerSlot slot : getOccupiedPlayerSlots())
		{
			clients.add(slot.getRole());
		}
		return clients;
	}

	public Collection<IClient> getClients()
	{
		LinkedList<IClient> clients = new LinkedList<IClient>();
		for (PlayerRole slot : getPlayers())
		{
			clients.add(slot.getClient());
		}
		return clients;
	}

	public void addObserver(Client source)
	{
		ObserverRole role = new ObserverRole(source, this);
		source.addRole(role);
		this.observers.add(role);
		source.send(new ObservationResponse(getId()));
	}

	public synchronized void onReservationClaimed(Client source,
			PlayerSlot result) throws RescueableClientException
	{
		fillSlot(result, source);
	}

	public synchronized void pause(boolean pause)
	{
		if (isOver())
		{
			logger.warn("Game is already over and can't be paused.");
		}

		if (!(this.game instanceof IPauseable))
		{
			logger.warn("Game isn't pausable.");
			return;
		}

		if (pause == isPaused())
		{
			logger.warn("Dropped unnecessary PAUSE toggle from {} to {}.",
					isPaused(), pause);
			return;
		}

		logger.info("Switching PAUSE from {} to {}.", isPaused(), pause);
		this.paused = pause;
		IPauseable pausableGame = (IPauseable) this.game;
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
	 *            If true, game will be started even if there are not enoug
	 *            players to complete the game. This should result in a
	 *            GameOver.
	 * @throws RescueableClientException
	 */
	public synchronized void step(boolean forced)
			throws RescueableClientException
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

		if (this.game instanceof IPauseable)
		{
			if (isPaused())
			{
				logger.info("Stepping.");
				((IPauseable) this.game).afterPause();
			}
			else
			{
				logger.warn("Can't step if the game is not paused.");
			}
		}
		else
		{
			logger.warn("Game isn't pausable.");
		}
	}

	public void cancel()
	{
		logger.warn("Game couldn't be canceled.");
		// TODO:
		// this.game.destroy();
	}

	@Override
	public void onPaused(IPlayer nextPlayer)
	{
		observerBroadcast(new RoomPacket(getId(), new GamePausedEvent(
				nextPlayer)));
	}

	public void openSlots(List<SlotDescriptor> descriptors)
			throws TooManyPlayersException
	{
		this.setSize(descriptors.size());

		for (int i = 0; i < descriptors.size(); i++)
		{
			this.playerSlots.get(i).setDescriptor(descriptors.get(i));
		}
	}

	public boolean isPrepared()
	{
		return this.prepared;
	}

	public boolean isOver()
	{
		return getStatus() == GameStatus.OVER;
	}

	public boolean isPaused()
	{
		return this.paused;
	}

	public GameStatus getStatus()
	{
		return this.status;
	}

	protected void setStatus(GameStatus status)
	{
		logger.info("Updating Status to {} (was: {})", status, getStatus());
		this.status = status;
	}

	public void removePlayer(IPlayer player)
	{
		logger.info("Removing {} from {}", player, this);
		this.game.onPlayerLeft(player);
	}

	public GameResult getResult()
	{
		return this.result;
	}

	protected void close()
	{
		if (!isOver())
		{
			kickAllClients();
			this.game.destroy();
		}
	}
}
