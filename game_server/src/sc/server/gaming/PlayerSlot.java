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

	public synchronized String reserve()
	{
		if (isReserved())
		{
			throw new IllegalStateException("Slot already reserved.");
		}
		else if(!isEmpty())
		{
			throw new IllegalStateException("This slot is already occupied.");
		}
		else
		{
			return ReservationManager.reserve(this);
		}
	}

	public void setClient(Client client)
	{
		if (!isEmpty())
		{
			throw new IllegalStateException("This slot is already occupied.");
		}

		this.role = new PlayerRole(client, this);
		client.addRole(this.role);
	}

	public void setPlayer(IPlayer player)
	{
		if (this.role == null)
		{
			throw new IllegalStateException(
					"Slot isn't linked to a Client yet.");
		}

		this.role.setPlayer(player);
	}

	public synchronized void free()
	{
		if (!this.reserved)
		{
			throw new IllegalStateException("This slot isn't reserved.");
		}

		this.reserved = false;
	}
}
