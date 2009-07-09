package sc.protocol;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.helpers.IAsyncResult;
import sc.networking.TcpNetwork;
import sc.protocol.requests.AuthenticateRequest;
import sc.protocol.requests.JoinPreparedRoomRequest;
import sc.protocol.requests.JoinRoomRequest;
import sc.protocol.requests.PrepareGameRequest;
import sc.protocol.responses.GamePrepared;
import sc.protocol.responses.JoinedGame;
import sc.protocol.responses.RoomLeft;

import com.thoughtworks.xstream.XStream;

/**
 * Sample client to be used in the SimpleClient library.
 * 
 * @author Marcel
 * 
 */
public abstract class LobbyClient extends XStreamClient
{
	protected final String				defaultGameType;
	private static final Logger			logger	= LoggerFactory
														.getLogger(LobbyClient.class);
	private static final List<String>	rooms	= new LinkedList<String>();

	public LobbyClient(String gameType, XStream xstream, String host, int port)
			throws IOException
	{
		super(xstream, new TcpNetwork(new Socket(host, port)));
		prepareXStream(xstream);
		this.defaultGameType = gameType;
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
			RoomPacket packet = (RoomPacket) o;
			if (packet.getData() instanceof MementoPacket)
			{
				MementoPacket statePacket = (MementoPacket) packet.getData();
				onNewState(packet.getRoomId(), statePacket.getState());
			}
			else
			{
				onRoomMessage(packet.getRoomId(), packet.getData());
			}
		}
		else if (o instanceof GamePrepared)
		{
			GamePrepared preparation = (GamePrepared) o;
			onGamePrepared(preparation.getGameId());
		}
		else if (o instanceof JoinedGame)
		{
			rooms.add(((JoinedGame) o).getRoomId());
		}
		else if (o instanceof RoomLeft)
		{
			rooms.remove(((RoomLeft) o).getRoomId());
		}
		else if (o instanceof ErrorResponse)
		{
			ErrorResponse response = (ErrorResponse) o;
			logger
					.warn("{} caused the following error: {}", response,
							response);
			onError(response);
		}
		else
		{
			onCustomObject(o);
		}
	}

	protected void onGamePrepared(String gameId)
	{
		// can be overridden
	}

	public void authenticate(String password)
	{
		send(new AuthenticateRequest(password));
	}

	public void observeGame(String gameId, String passphrase)
	{
		send(new ObservationRequest(gameId, passphrase));
	}

	public void prepareGame(String gameType, int playerCount)
	{
		send(new PrepareGameRequest(gameType, playerCount));
	}

	protected void onCustomObject(Object o)
	{
		logger.warn("Couldn't process message {}.", o);
	}

	protected abstract void onNewState(String roomId, Object state);

	protected abstract void onError(ErrorResponse response);

	public void sendMessageToRoom(String roomId, Object o)
	{
		this.send(new RoomPacket(roomId, o));
	}

	protected abstract void onRoomMessage(String roomId, Object data);

	protected abstract Collection<Class<? extends Object>> getProtocolClasses();

	public void joinPreparedGame(String reservation)
	{
		this.send(new JoinPreparedRoomRequest(reservation));
	}

	public void joinAnyGame()
	{
		this.send(new JoinRoomRequest(this.defaultGameType));
	}
}
