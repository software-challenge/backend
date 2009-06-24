package sc.server.network;

import sc.server.protocol.InboundPacket;

public interface IClientListener
{
    public void onClientDisconnected(Client source);
	void onPacketReceived(Client source, InboundPacket packet);
}
