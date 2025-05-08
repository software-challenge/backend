package sc.plugin2010;

import java.io.IOException;

/**
 * Der Client für das Hase- und Igel Plugin.
 * 
 * @author rra
 * @since Jul 5, 2009
 * 
 */
public class Client extends AbstractClient
{
	public Client(String host, int port, EPlayerId id) throws IOException
	{
		super(host, port, id);
	}

	@Override
	public void onGameJoined(String roomId)
	{
		// ignore
	}

	@Override
	public void onGameLeft(String roomId)
	{
		// ignore
	}
}
