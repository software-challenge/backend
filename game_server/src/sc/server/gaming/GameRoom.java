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
import sc.protocol.responses.RoomPacket;
import sc.server.network.Client;
import sc.server.plugins.GamePluginInstance;
import sc.shared.GameResult;
import sc.shared.PlayerScore;
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
	private final GamePluginInstance	provider;
	private final IGameInstance			game;
	private List<ObserverRole>			observers	= new LinkedList<ObserverRole>();
	private List<PlayerSlot>			playerSlots	= new ArrayList<PlayerSlot>(
															2);
	private boolean						isOver		= false;
	private boolean						paused		= false;

	public GameRoom(String id, GamePluginInstance provider, IGameInstance game)
	{
		if (provider == null)
		{
			throw new IllegalArgumentException("Provider must not be null");
		}

		this.id = id;
		this.provider = provider;
		this.game = game;
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
	public void onGameOver(Map<IPlayer, PlayerScore> results)
	{
		this.isOver = true;

		ScoreDefinition definition = getProvider().getPlugin()
				.getScoreDefinition();
		List<PlayerScore> scores = new LinkedList<PlayerScore>();

		// restore order
		for (PlayerRole player : getPlayers())
		{
			PlayerScore score = results.get(player.getPlayer());

			if (score == null)
			{
				throw new RuntimeException("GameScore was not complete!");
			}

			if (score.size() != definition.size())
			{
				throw new RuntimeException("ScoreSize did not match Definition");
			}

			scores.add(score);
		}

		GameResult result = new GameResult(definition, scores);
		logger.info("The game {} is over. (regular={})", getId(), result
				.isRegular());
		broadcast(result);
		kickAllClients();
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

	@Override
	public void onPlayerJoined(IPlayer player)
	{
		// not interesting
	}

	@Override
	public void onPlayerLeft(IPlayer player)
	{
		// not interesting
	}

	public String getId()
	{
		return this.id;
	}

	public synchronized boolean join(Client client)
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
	{
		openSlot.setClient(client);
		IPlayer player;
		try
		{
			player = getGame().onPlayerJoined();
		}
		catch (TooManyPlayersException e)
		{
			// should't happen
			throw new RuntimeException(e);
		}
		player.setDisplayName(openSlot.getDescriptor().getDisplayName());
		player.setShouldBePaused(openSlot.getDescriptor().isShouldBePaused());
		player.setCanTimeout(openSlot.getDescriptor().isCanTimeout());
		openSlot.setPlayer(player);
		client.send(new JoinGameResponse(getId()));
		startIfReady();
	}

	private void startIfReady()
	{
		if (this.game.ready())
		{
			this.game.start();
			logger.info("Started the game.");
		}
		else
		{
			logger.info("Game isn't ready yet.");
		}
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
		if (this.isOver)
		{
			throw new RescueableClientException(
					"Game is already over, but got data: " + data.getClass());
		}
		else
		{
			this.game.onAction(resolvePlayer(source), data);
		}
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

	public Collection<Client> getClients()
	{
		LinkedList<Client> clients = new LinkedList<Client>();
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
	}

	public synchronized void onReservationClaimed(Client source,
			PlayerSlot result)
	{
		fillSlot(result, source);
	}

	public synchronized void pause(boolean pause)
	{
		if (this.game instanceof IPauseable)
		{
			IPauseable pausableGame = (IPauseable) this.game;
			if (pause == this.paused)
			{
				logger.warn("Dropped unnecessary PAUSE toggle from {} to {}.",
						this.paused, pause);
			}
			else
			{
				logger.info("Switching PAUSE from {} to {}.", this.paused, pause);
				this.paused = pause;
				pausableGame.setPauseMode(pause);

				if (!pause) // continue execution
				{
					pausableGame.afterPause();
				}
			}
		}
		else
		{
			logger.warn("Game isn't pausable.");
		}
	}

	public synchronized void step()
	{
		if (this.game instanceof IPauseable)
		{
			if (this.paused)
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
}
