package sc.server.gaming;

import java.util.ArrayList;
import java.util.List;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGameListener;
import sc.api.plugins.IPlayer;
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
	private List<PlayerSlot>	playerSlots = new ArrayList<PlayerSlot>(2);

	public GameRoom(GamePluginInstance provider)
	{
		this(provider, null);
	}
	
	public GameRoom(GamePluginInstance provider, IGameInstance game)
	{
		if(provider == null)
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
		for(PlayerSlot slot : playerSlots) {
			if(slot.isEmpty())
			{
				slot.setClient(client);
				return true;
			}
		}
		
		if(playerSlots.size() < this.provider.getPlugin().getMaximumPlayerCount())
		{
			PlayerSlot slot = new PlayerSlot(this);
			slot.setClient(client);
			return true;
		}
		
		return false;
	}
}
