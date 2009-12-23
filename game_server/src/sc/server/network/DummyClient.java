package sc.server.network;

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
	public void send(Object toSend)
	{
		// ignore
	}

	@Override
	public void sendAsynchronous(Object packet)
	{
		// ignore
	}
}
