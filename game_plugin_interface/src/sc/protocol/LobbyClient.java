package sc.protocol;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.networking.INetworkInterface;
import sc.protocol.requests.JoinRoomRequest;
import sc.protocol.requests.RoomRequest;
import sc.protocol.responses.RoomResponse;

import com.thoughtworks.xstream.XStream;

/**
 * Sample client to be used in the SimpleClient library.
 * 
 * @author Marcel
 * 
 */
public abstract class LobbyClient extends XStreamClient
{
	final String				gameType;
	private static final Logger	logger	= LoggerFactory
												.getLogger(LobbyClient.class);

	public LobbyClient(String gameType, XStream xstream,
			INetworkInterface networkInterface) throws IOException
	{
		super(xstream, networkInterface);
		this.gameType = gameType;
	}

	public static void register(XStream xStream)
	{
		LobbyProtocol.registerMessages(xStream);
	}

	@Override
	protected final void onObject(Object o)
	{
		if (o instanceof RoomResponse)
		{
			onRoomMessage(((RoomResponse) o).getRoomId(), ((RoomResponse) o)
					.getData());
		}
		else
		{
			logger.warn("Couldn't process message {}.", o);
		}
	}

	public void sendMessageToRoom(String roomId, Object o)
	{
		this.send(new RoomRequest(roomId, o));
	}

	public void joinAnyGame()
	{
		this.send(new JoinRoomRequest(this.gameType));
	}

	protected abstract void onRoomMessage(String roomId, Object data);
}
