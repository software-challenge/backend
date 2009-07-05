package sc.plugin2010;

import java.io.IOException;

import sc.networking.INetworkInterface;
import sc.protocol.LobbyClient;

import com.thoughtworks.xstream.XStream;

/**
 * Der Client für das Hase- und Igel Plugin.
 * 
 * @author rra
 * @since Jul 5, 2009
 * 
 */
public class Client extends LobbyClient
{
	public Client(String gameType, XStream xstream, String host, int port)
			throws IOException
	{
		super(gameType, xstream, host, port);
	}

	@Override
	protected void onRoomMessage(String roomId, Object data)
	{
		// TODO Auto-generated method stub

	}
}
