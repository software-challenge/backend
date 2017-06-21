package sc.server.network;

import sc.api.plugins.exceptions.RescuableClientException;

public interface IClientListener
{
	public void onClientDisconnected(Client source);

	/**
	 * Invoked when new data is received and ready to be processed.
	 * 
	 * @param source
	 * @param packet
	 * @throws RescuableClientException
	 */
	void onRequest(Client source, PacketCallback packet)
			throws RescuableClientException;
	
	void onError(Client source, Object packet);
}
