package sc.server.network;

import sc.server.RescueableClientException;

public interface IClientListener
{
    public void onClientDisconnected(Client source);
	void onRequest(Client source, Object packet) throws RescueableClientException;
}
