package sc.server.gaming;

import sc.server.network.Client;
import sc.server.network.IClientRole;

public class ObserverRole implements IClientRole
{
	private Client		client;
	private GameRoom	gameRoom;

	public ObserverRole(Client owner, GameRoom gameRoom)
	{
		this.client = owner;
		this.gameRoom = gameRoom;
	}

	@Override
	public Client getClient()
	{
		return this.client;
	}

	public GameRoom getGameRoom()
	{
		return this.gameRoom;
	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub
	}
}
