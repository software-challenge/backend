package sc.server.gaming;

import sc.api.plugins.IPlayer;
import sc.api.plugins.IPlayerListener;
import sc.server.network.Client;
import sc.server.network.IClientRole;
import sc.server.network.PacketCallback;

public class PlayerRole implements IClientRole, IPlayerListener
{
	private Client		client;

	private IPlayer		player;

	private PlayerSlot	playerSlot;

	public PlayerRole(Client owner, PlayerSlot slot)
	{
		this.client = owner;
		this.playerSlot = slot;
	}

	@Override
	public void onClientDisconnected(Client source)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequest(Client source, PacketCallback callback)
	{
		// TODO Auto-generated method stub
	}

	public Client getClient()
	{
		return this.client;
	}

	public IPlayer getPlayer()
	{
		return this.player;
	}

	public PlayerSlot getPlayerSlot()
	{
		return this.playerSlot;
	}
}
