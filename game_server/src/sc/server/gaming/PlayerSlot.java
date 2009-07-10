package sc.server.gaming;

import sc.api.plugins.IPlayer;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.protocol.responses.JoinGameResponse;
import sc.server.network.Client;

public class PlayerSlot
{
	private PlayerRole		role;
	private final GameRoom	room;
	private boolean			reserved;

	public PlayerSlot(GameRoom room)
	{
		if (room == null)
		{
			throw new IllegalStateException("Room must not be null.");
		}

		this.room = room;
	}

	public PlayerRole getRole()
	{
		return role;
	}

	public GameRoom getRoom()
	{
		return room;
	}

	public boolean isEmpty()
	{
		return this.role == null;
	}

	public boolean isReserved()
	{
		return this.reserved;
	}

	public String reserve()
	{
		if (isReserved())
		{
			throw new RuntimeException("Slot already reserved.");
		}
		else
		{
			return ReservationManager.reserve(this);
		}
	}

	public void setClient(Client client)
	{
		if (this.role != null)
		{
			throw new IllegalStateException("This slot is already occupied.");
		}

		IPlayer player;
		try
		{
			player = getRoom().getGame().onPlayerJoined();
		}
		catch (TooManyPlayersException e)
		{
			// not expected to happen
			throw new RuntimeException(e);
		}

		this.role = new PlayerRole(client, this);
		client.addRole(this.role);
		client.send(new JoinGameResponse(getRoom().getId()));
		this.role.setPlayer(player);
	}
}
