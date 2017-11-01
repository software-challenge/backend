package sc.server.network;

import sc.protocol.responses.ProtocolMessage;

/**
 * A fake client to fill empty player slots.
 * 
 * @author Marcel
 * 
 */
public class DummyClient implements IClient
{
	@Override
	public void addRole(IClientRole role)
	{
		// ignore
	}

	@Override
	public void send(ProtocolMessage toSend)
	{
		// ignore
	}
}
