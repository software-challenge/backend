package sc.protocol;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.networking.TcpNetwork;
import sc.protocol.requests.AuthenticateRequest;
import sc.protocol.requests.JoinPreparedRoomRequest;
import sc.protocol.requests.JoinRoomRequest;
import sc.protocol.requests.PrepareGameRequest;
import sc.protocol.responses.JoinGameResponse;
import sc.protocol.responses.LeftGameEvent;
import sc.protocol.responses.PrepareGameResponse;

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
	private static final Logger			logger			= LoggerFactory
																.getLogger(LobbyClient.class);
	private static final List<String>	rooms			= new LinkedList<String>();
	private ClientType					type			= ClientType.UNDEFINED;
	private Map<String, List<Object>>	observations	= new HashMap<String, List<Object>>();

	public LobbyClient(String gameType, XStream xstream, String host, int port)
			throws IOException
	{
		super(xstream, new TcpNetwork(new Socket(host, port)));
		prepareXStream(xstream);
		this.defaultGameType = gameType;
	}

	private void prepareXStream(XStream toConfigure)
	{
		LobbyProtocol.registerMessages(toConfigure, getProtocolClasses());
	}

	public List<String> getRooms()
	{
		return Collections.unmodifiableList(rooms);
	}

	private void setType(ClientType newType)
	{
		if (newType == null || newType == ClientType.UNDEFINED)
		{
			throw new IllegalArgumentException();
		}

		if (this.type == ClientType.UNDEFINED)
		{
			this.type = newType;
		}
		else
		{
			// can't switch type lateron
			if (this.type != newType)
			{
				throw new IllegalStateException(
						"Can't switch types during execution.");
			}
		}
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
				addObservationIfObserver(packet.getRoomId(), statePacket);
			}
			else
			{
				onRoomMessage(packet.getRoomId(), packet.getData());
			}
		}
		else if (o instanceof PrepareGameResponse)
		{
			PrepareGameResponse preparation = (PrepareGameResponse) o;
			onGamePrepared(preparation.getRoomId());
		}
		else if (o instanceof JoinGameResponse)
		{
			rooms.add(((JoinGameResponse) o).getRoomId());
		}
		else if (o instanceof LeftGameEvent)
		{
			rooms.remove(((LeftGameEvent) o).getRoomId());
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

	private void addObservationIfObserver(String roomId, Object observation)
	{
		if (this.type == ClientType.OBSERVER)
		{
			List<Object> states = this.observations.get(roomId);

			if (states == null)
			{
				states = new LinkedList<Object>();
				this.observations.put(roomId, states);
			}

			states.add(observation);
		}
	}

	private void writeObservationsToStream(String roomId, OutputStream to)
			throws IOException
	{
		List<Object> states = this.observations.get(roomId);

		if (states == null)
		{
			logger.warn("No observations available for roomId={}", roomId);
		}
		else
		{
			ObjectOutputStream output = this.xStream
					.createObjectOutputStream(to);

			for (Object state : states)
			{
				output.writeObject(state);
			}
		}
	}

	protected void onGamePrepared(String roomId)
	{
		// can be overridden
	}

	public void authenticate(String password)
	{
		setType(ClientType.ADMINISTRATOR);
		send(new AuthenticateRequest(password));
	}

	public void observeGame(String gameId, String passphrase)
	{
		setType(ClientType.OBSERVER);
		send(new ObservationRequest(gameId, passphrase));
	}

	public void prepareGame(String gameType, int playerCount)
	{
		setType(ClientType.ADMINISTRATOR);
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
		setType(ClientType.PLAYER);
		this.send(new JoinPreparedRoomRequest(reservation));
	}

	public void joinAnyGame()
	{
		setType(ClientType.PLAYER);
		this.send(new JoinRoomRequest(this.defaultGameType));
	}
}
