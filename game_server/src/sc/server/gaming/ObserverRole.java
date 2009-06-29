package sc.server.gaming;

import sc.server.network.Client;
import sc.server.network.IClientRole;

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
	public void onRequest(Client source, Object packet)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Client getClient()
	{
		return this.client;
	}

}
