package sc.server.gaming;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGameListener;
import sc.api.plugins.IPlayer;
import sc.api.plugins.TooManyPlayersException;
import sc.server.network.Client;
import sc.server.plugins.GamePluginInstance;

/**
 * A wrapper for an actual <code>GameInstance</code>. GameInstances are provided
 * by the plugins. Additional mapping data (Client2Player) will be stored here.
 */
public class GameRoom implements IGameListener
{
	private int					id;
	private GamePluginInstance	provider;
	private IGameInstance		game;
	private List<ObserverRole>	observers;
	private List<PlayerSlot>	playerSlots	= new ArrayList<PlayerSlot>(2);

	public GameRoom(GamePluginInstance provider)
	{
		this(provider, null);
	}

	public GameRoom(GamePluginInstance provider, IGameInstance game)
	{
		if (provider == null)
		{
			throw new IllegalArgumentException("Provider must not be null");
		}

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

	public int getId()
	{
		return this.id;
	}

	public boolean join(Client client)
	{
		for (PlayerSlot slot : playerSlots)
		{
			if (slot.isEmpty() && !slot.isReserved())
			{
				slot.setClient(client);
				return true;
			}
		}

		if (playerSlots.size() < getMaximumPlayerCount())
		{
			PlayerSlot slot = new PlayerSlot(this);
			slot.setClient(client);
			return true;
		}

		return false;
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
}
