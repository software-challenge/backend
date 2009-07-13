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

import sc.api.plugins.IPlayer;
import sc.api.plugins.host.IRequestResult;
import sc.networking.TcpNetwork;
import sc.protocol.clients.ControllingClient;
import sc.protocol.clients.ObservingClient;
import sc.protocol.requests.AuthenticateRequest;
import sc.protocol.requests.FreeReservationRequest;
import sc.protocol.requests.JoinPreparedRoomRequest;
import sc.protocol.requests.JoinRoomRequest;
import sc.protocol.requests.PrepareGameRequest;
import sc.protocol.responses.GamePausedEvent;
import sc.protocol.responses.JoinGameResponse;
import sc.protocol.responses.LeftGameEvent;
import sc.protocol.responses.PrepareGameResponse;
import sc.shared.GameResult;

import com.thoughtworks.xstream.XStream;

/**
 * Sample client to be used in the SimpleClient library.
 * 
 * @author Marcel
 * 
 */
public final class LobbyClient extends XStreamClient implements IPollsHistory
{
	private static final Logger					logger				= LoggerFactory
																			.getLogger(LobbyClient.class);
	private static final List<String>			rooms				= new LinkedList<String>();
	private final AsyncResultManager			asyncManager		= new AsyncResultManager();
	private final List<ILobbyClientListener>	listeners			= new LinkedList<ILobbyClientListener>();
	private final List<IHistoryListener>		historyListeners	= new LinkedList<IHistoryListener>();

	public static final int						DEFAULT_PORT		= 13050;
	public static final String					DEFAULT_HOST		= "localhost";

	public LobbyClient(XStream xStream) throws IOException
	{
		this(xStream, null);
	}

	public LobbyClient(XStream xStream, Collection<Class<?>> protocolClasses)
			throws IOException
	{
		this(xStream, protocolClasses, DEFAULT_HOST, DEFAULT_PORT);
	}

	public LobbyClient(XStream xstream, Collection<Class<?>> protocolClasses,
			String host, int port) throws IOException
	{
		super(xstream, new TcpNetwork(new Socket(host, port)));
		LobbyProtocol.registerMessages(xstream, protocolClasses);
	}

	public List<String> getRooms()
	{
		return Collections.unmodifiableList(rooms);
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
			String roomId = packet.getRoomId();
			if (packet.getData() instanceof MementoPacket)
			{
				MementoPacket statePacket = (MementoPacket) packet.getData();
				onNewState(roomId, statePacket.getState());
			}
			else if (packet.getData() instanceof GameResult)
			{
				onGameOver(roomId, (GameResult) packet.getData());
			}
			else if (packet.getData() instanceof GamePausedEvent)
			{
				onGamePaused(roomId, ((GamePausedEvent) packet.getData()).getNextPlayer());
			}
			else
			{
				onRoomMessage(roomId, packet.getData());
			}
		}
		else if (o instanceof PrepareGameResponse)
		{
			PrepareGameResponse preparation = (PrepareGameResponse) o;
			onGamePrepared(preparation);
		}
		else if (o instanceof JoinGameResponse)
		{
			String roomId = ((JoinGameResponse) o).getRoomId();
			rooms.add(roomId);
			onGameJoined(roomId);
		}
		else if (o instanceof LeftGameEvent)
		{
			String roomId = ((LeftGameEvent) o).getRoomId();
			rooms.remove(roomId);
			onGameLeft(roomId);
		}
		else if (o instanceof ErrorResponse)
		{
			ErrorResponse response = (ErrorResponse) o;
			if (response.getOriginalRequest() != null)
			{
				logger.warn("The request {} caused the following error: {}",
						response.getOriginalRequest().getClass(), response
								.getMessage());
			}
			else
			{
				logger.warn("An error occured: {}", response.getMessage());
			}
			onError(response);
		}
		else
		{
			onCustomObject(o);
		}
	}

	private void onGamePaused(String roomId, IPlayer nextPlayer)
	{
		for (ILobbyClientListener listener : this.listeners)
		{
			listener.onGamePaused(roomId, nextPlayer);
		}
	}

	private void onGameOver(String roomId, GameResult data)
	{
		for (ILobbyClientListener listener : this.listeners)
		{
			listener.onGameOver(roomId, data);
		}
	}

	private void onGameLeft(String roomId)
	{
		for (ILobbyClientListener listener : this.listeners)
		{
			listener.onGameLeft(roomId);
		}
	}

	private void onGameJoined(String roomId)
	{
		for (ILobbyClientListener listener : this.listeners)
		{
			listener.onGameJoined(roomId);
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

	protected void onGamePrepared(PrepareGameResponse response)
	{
		for (ILobbyClientListener listener : this.listeners)
		{
			listener.onGamePrepared(response);
		}
	}

	public void authenticate(String password)
	{
		send(new AuthenticateRequest(password));
	}

	public RequestResult<PrepareGameResponse> prepareGameAndWait(
			String gameType, int playerCount) throws InterruptedException
	{
		return blockingRequest(new PrepareGameRequest(gameType, playerCount),
				PrepareGameResponse.class);
	}

	public void prepareGame(String gameType, int playerCount)
	{
		send(new PrepareGameRequest(gameType, playerCount));
	}

	protected void onCustomObject(Object o)
	{
		logger.warn("Couldn't process message {}.", o);
	}

	protected void onNewState(String roomId, Object state)
	{
		for (ILobbyClientListener listener : this.listeners)
		{
			listener.onNewState(roomId, state);
		}

		for (IHistoryListener listener : this.historyListeners)
		{
			listener.onNewState(roomId, state);
		}
	}

	protected void onError(ErrorResponse error)
	{
		for (ILobbyClientListener listener : this.listeners)
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
		for (ILobbyClientListener listener : this.listeners)
		{
			listener.onRoomMessage(roomId, data);
		}
	}

	public void joinPreparedGame(String reservation)
	{
		this.send(new JoinPreparedRoomRequest(reservation));
	}

	public void joinAnyGame(String gameType)
	{
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

	public IControllableGame observeAndControl(PrepareGameResponse handle)
	{
		IControllableGame result = new ControllingClient(this, handle.getRoomId());
		this.start();
		this.send(new ObservationRequest(handle.getRoomId(), ""));
		result.pause();
		return result;
	}

	public IControllableGame observe(PrepareGameResponse handle)
	{
		IControllableGame result = new ObservingClient(this, handle.getRoomId());
		this.start();
		this.send(new ObservationRequest(handle.getRoomId(), ""));
		return result;
	}

	@Override
	public void addListener(IHistoryListener listener)
	{
		this.historyListeners.add(listener);
	}

	@Override
	public void removeListener(IHistoryListener listener)
	{
		this.historyListeners.remove(listener);
	}

	public void freeReservation(String reservation)
	{
		this.send(new FreeReservationRequest(reservation));
	}
}
