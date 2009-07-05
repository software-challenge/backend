package sc.plugin2010;

import java.io.IOException;

import com.thoughtworks.xstream.XStream;

import sc.networking.INetworkInterface;
import sc.protocol.LobbyClient;

/**
 * Der Client für das Hase- und Igel Plugin
 * 
 * @author rra
 * @since Jul 5, 2009
 *
 */
public class Client extends LobbyClient
{
	public Client(String gameType, XStream xstream,
			INetworkInterface networkInterface) throws IOException
	{
		super(gameType, xstream, networkInterface);
	}

	@Override
	protected void onRoomMessage(String roomId, Object data)
	{
		// TODO Auto-generated method stub
		
	}
}
