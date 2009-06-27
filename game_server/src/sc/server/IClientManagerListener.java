package sc.server;

import sc.server.network.Client;

public interface IClientManagerListener
{
	public void onClientConnected(Client client);
}
