package sc.server.gaming;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGameListener;
import sc.api.plugins.IPlayer;
import sc.api.plugins.RescueableClientException;
import sc.api.plugins.TooManyPlayersException;
import sc.server.network.Client;
import sc.server.plugins.GamePluginInstance;

/**
 * A wrapper for an actual <code>GameInstance</code>. GameInstances are provided
 * by the plugins. Additional mapping data (Client2Player) will be stored here.
 */
public class GameRoom implements IGameListener
{
	private final String		id;
	private GamePluginInstance	provider;
	private IGameInstance		game;
	private List<ObserverRole>	observers;
	private List<PlayerSlot>	playerSlots	= new ArrayList<PlayerSlot>(2);

	public GameRoom(String id, GamePluginInstance provider)
	{
		this(id, provider, null);
	}

	public GameRoom(String id, GamePluginInstance provider, IGameInstance game)
	{
		if (provider == null)
		{
			throw new IllegalArgumentException("Provider must not be null");
		}

		this.id = id;
		this.provider = provider;
		this.game = game;
	}

	public GamePluginInstance getProvider()
	{
		return provider;
	}

	public IGameInstance getGame()
	{
		return game;
	}

	@Override
	public void onGameOver()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPlayerJoined(IPlayer player)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPlayerLeft(IPlayer player)
	{
		// TODO Auto-generated method stub

	}

	public String getId()
	{
		return this.id;
	}

	public boolean join(Client client)
	{
		PlayerSlot openSlot = null;
		
		for (PlayerSlot slot : playerSlots)
		{
			if (slot.isEmpty() && !slot.isReserved())
			{
				openSlot = slot;
				break;
			}
		}

		if (playerSlots.size() < getMaximumPlayerCount())
		{
			openSlot = new PlayerSlot(this);
			this.playerSlots.add(openSlot);
		}
		
		if(openSlot != null)
		{
			openSlot.setClient(client);
			game.start();
			return true;
		}
		else
		{			
			return false;
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

	public void setSize(int playerCount) throws TooManyPlayersException
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

	public List<String> reserveAllSlots()
	{
		List<String> result = new ArrayList<String>(playerSlots.size());

		for (PlayerSlot playerSlot : playerSlots)
		{
			result.add(playerSlot.reserve());
		}

		return result;
	}

	public void onEvent(Client source, Object data)
			throws RescueableClientException
	{
		this.game.actionReceived(resolvePlayer(source), data);
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
				+ id);
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
}
