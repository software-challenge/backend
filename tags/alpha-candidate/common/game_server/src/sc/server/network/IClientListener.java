package sc.server.network;

import sc.api.plugins.exceptions.RescueableClientException;

public interface IClientListener
{
    public void onClientDisconnected(Client source);
	void onRequest(Client source, PacketCallback packet) throws RescueableClientException;
}
