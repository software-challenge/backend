package sc.server.network;

import sc.api.plugins.exceptions.RescueableClientException;

public interface IClientListener
{
	public void onClientDisconnected(Client source);

	/**
	 * Invoked when new data is received and ready to be processed.
	 * 
	 * @param source
	 * @param packet
	 * @throws RescueableClientException
	 */
	void onRequest(Client source, PacketCallback packet)
			throws RescueableClientException;
	
	void onError(Client source, Object packet);
}
