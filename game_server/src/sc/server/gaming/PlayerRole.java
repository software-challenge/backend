package sc.server.gaming;

import sc.api.plugins.IPlayer;
import sc.api.plugins.host.IPlayerListener;
import sc.protocol.responses.RoomPacket;
import sc.server.network.IClient;
import sc.server.network.IClientRole;

public class PlayerRole implements IClientRole, IPlayerListener
{
	private IClient		client;

	private IPlayer		player;

	private PlayerSlot	playerSlot;

	public PlayerRole(IClient owner, PlayerSlot slot)
	{
		this.client = owner;
		this.playerSlot = slot;
	}

	public IClient getClient()
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

	@Override
	public void onPlayerEvent(Object o)
	{
		this.client.send(new RoomPacket(
				this.getPlayerSlot().getRoom().getId(), o));
	}

	public void setPlayer(IPlayer player)
	{
		this.player = player;
		this.player.addPlayerListener(this);
	}

	@Override
	public void close()
	{
		this.getPlayerSlot().close();
	}
}
