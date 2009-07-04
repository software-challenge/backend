package sc.server.gaming;

import sc.server.network.Client;
import sc.server.network.IClientRole;
import sc.server.network.PacketCallback;

public class ObserverRole implements IClientRole
{
	private Client	client;

	public ObserverRole(Client owner)
	{
		this.client = owner;
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

	@Override
	public Client getClient()
	{
		return this.client;
	}

}
