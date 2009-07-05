package sc.protocol;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.networking.TcpNetwork;
import sc.protocol.requests.JoinPreparedRoomRequest;
import sc.protocol.requests.JoinRoomRequest;
import sc.protocol.responses.JoinedGame;

import com.thoughtworks.xstream.XStream;

/**
 * Sample client to be used in the SimpleClient library.
 * 
 * @author Marcel
 * 
 */
public abstract class LobbyClient extends XStreamClient
{
	final String						gameType;
	private static final Logger			logger	= LoggerFactory
														.getLogger(LobbyClient.class);
	private static final List<String>	rooms	= new LinkedList<String>();

	public LobbyClient(String gameType, XStream xstream, String host, int port)
			throws IOException
	{
		super(xstream, new TcpNetwork(new Socket(host, port)));
		prepareXStream(xstream);
		this.gameType = gameType;
	}

	private void prepareXStream(XStream xStream)
	{
		LobbyProtocol.registerMessages(xStream, getProtocolClasses());
	}

	public List<String> getRooms()
	{
		return Collections.unmodifiableList(rooms);
	}

	@Override
	protected final void onObject(Object o)
	{
		if (o instanceof RoomPacket)
		{
			onRoomMessage(((RoomPacket) o).getRoomId(), ((RoomPacket) o)
					.getData());
		}
		else if (o instanceof JoinedGame)
		{
			rooms.add(((JoinedGame) o).getRoomId());
		}
		else
		{
			logger.warn("Couldn't process message {}.", o);
		}
	}

	public void sendMessageToRoom(String roomId, Object o)
	{
		this.send(new RoomPacket(roomId, o));
	}

	public void joinPreparedGame(String reservation)
	{
		this.send(new JoinPreparedRoomRequest(reservation));
	}

	public void joinAnyGame()
	{
		this.send(new JoinRoomRequest(this.gameType));
	}

	protected abstract void onRoomMessage(String roomId, Object data);

	protected abstract Collection<Class<? extends Object>> getProtocolClasses();
}
