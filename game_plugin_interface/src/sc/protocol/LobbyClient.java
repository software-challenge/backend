package sc.protocol;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.helpers.IRequestResult;
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
public final class LobbyClient extends XStreamClient
{
	private static final Logger					logger			= LoggerFactory
																		.getLogger(LobbyClient.class);
	private static final List<String>			rooms			= new LinkedList<String>();
	private ClientType							type			= ClientType.UNDEFINED;
	private final Map<String, List<Object>>		observations	= new HashMap<String, List<Object>>();
	private final AsyncResultManager			asyncManager	= new AsyncResultManager();
	private final List<ILobbyClientListener>	listeners		= new LinkedList<ILobbyClientListener>();

	public LobbyClient(XStream xstream, String host, int port)
			throws IOException
	{
		super(xstream, new TcpNetwork(new Socket(host, port)));
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
		if (o == null)
		{
			logger.warn("Received null object.");
			return;
		}

		invokeHandlers(o);

		if (o instanceof RoomPacket)
		{
			RoomPacket packet = (RoomPacket) o;
			if (packet.getData() instanceof MementoPacket)
			{
				MementoPacket statePacket = (MementoPacket) packet.getData();
				addObservationIfObserver(packet.getRoomId(), statePacket);
				onNewState(packet.getRoomId(), statePacket.getState());
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

	@SuppressWarnings("unchecked")
	private <T> void invokeHandlers(T o)
	{
		if (o == null)
		{
			throw new IllegalArgumentException("o was null");
		}

		if (o instanceof ErrorResponse)
		{
			Object originalRequest = ((ErrorResponse) o).getOriginalRequest();
			if (originalRequest != null)
			{
				this.asyncManager.invokeHandlers(originalRequest.getClass(),
						null, (ErrorResponse) o);
			}
			else
			{
				logger
						.warn("Couldn't invoke Handlers because OriginalRequest was null.");
			}
		}
		else
		{
			this.asyncManager.invokeHandlers((Class<T>) o.getClass(), o, null);
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

	public RequestResult<PrepareGameResponse> prepareGameAndWait(
			String gameType, int playerCount) throws InterruptedException
	{
		return blockingRequest(new PrepareGameRequest(gameType, playerCount),
				PrepareGameResponse.class);
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

	protected void onNewState(String roomId, Object state)
	{
		for(ILobbyClientListener listener : this.listeners)
		{
			listener.onNewState(roomId, state);
		}
	}

	protected void onError(ErrorResponse error)
	{
		for(ILobbyClientListener listener : this.listeners)
		{
			listener.onError(error);
		}
	}

	public void sendMessageToRoom(String roomId, Object o)
	{
		this.send(new RoomPacket(roomId, o));
	}

	protected void onRoomMessage(String roomId, Object data)
	{
		for(ILobbyClientListener listener : this.listeners)
		{
			listener.onRoomMessage(roomId, data);
		}
	}

	public void joinPreparedGame(String reservation)
	{
		setType(ClientType.PLAYER);
		this.send(new JoinPreparedRoomRequest(reservation));
	}

	public void joinAnyGame(String gameType)
	{
		setType(ClientType.PLAYER);
		this.send(new JoinRoomRequest(gameType));
	}

	protected <T> void request(IRequest<T> request, Class<T> response,
			IRequestResult<T> handler)
	{
		this.asyncManager.addHandler(response, handler);
		send(request);
	}

	protected <T> RequestResult<T> blockingRequest(IRequest<T> request,
			Class<T> response) throws InterruptedException
	{
		final RequestResult<T> requestResult = new RequestResult<T>();
		final Object beacon = new Object();
		synchronized (beacon)
		{
			IRequestResult<T> blockingHandler = new IRequestResult<T>() {

				@Override
				public void handleError(ErrorResponse e)
				{
					requestResult.setError(e);
					notifySemaphore();
				}

				public void operate(T result)
				{
					requestResult.setResult(result);
					notifySemaphore();
				}

				private void notifySemaphore()
				{
					synchronized (beacon)
					{
						beacon.notify();
					}
				}
			};
			request(request, response, blockingHandler);
			beacon.wait();
		}

		return requestResult;
	}

	public void addListener(ILobbyClientListener listener)
	{
		this.listeners.add(listener);
	}
	
	public void removeListener(ILobbyClientListener listener)
	{
		this.listeners.remove(listener);
	}
}
