package sc.server.gaming;

import sc.api.plugins.host.IPlayerListener;
import sc.framework.plugins.SimplePlayer;
import sc.protocol.responses.RoomPacket;
import sc.server.network.IClient;
import sc.server.network.IClientRole;

public class PlayerRole implements IClientRole, IPlayerListener
{
	private IClient		client;

	private SimplePlayer		player;

	private PlayerSlot	playerSlot;

	public PlayerRole(IClient owner, PlayerSlot slot)
	{
		this.client = owner;
		this.playerSlot = slot;
	}

	@Override
	public IClient getClient()
	{
		return this.client;
	}

	public SimplePlayer getPlayer()
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
				getPlayerSlot().getRoom().getId(), o));
	}

	public void setPlayer(SimplePlayer player)
	{
		this.player = player;
		this.player.addPlayerListener(this);
	}

	@Override
	public void close()
	{
		getPlayerSlot().close();
	}
}
