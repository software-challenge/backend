package sc.networking.clients;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

import sc.api.plugins.host.IRequestResult;
import sc.framework.plugins.SimplePlayer;
import sc.networking.INetworkInterface;
import sc.networking.TcpNetwork;
import sc.protocol.LobbyProtocol;
import sc.protocol.helpers.AsyncResultManager;
import sc.protocol.helpers.RequestResult;
import sc.protocol.requests.AuthenticateRequest;
import sc.protocol.requests.FreeReservationRequest;
import sc.protocol.requests.IRequest;
import sc.protocol.requests.JoinPreparedRoomRequest;
import sc.protocol.requests.JoinRoomRequest;
import sc.protocol.requests.ObservationRequest;
import sc.protocol.requests.PrepareGameRequest;
import sc.protocol.responses.ErrorResponse;
import sc.protocol.responses.GamePausedEvent;
import sc.protocol.responses.JoinGameResponse;
import sc.protocol.responses.LeftGameEvent;
import sc.protocol.responses.MementoPacket;
import sc.protocol.responses.ObservationResponse;
import sc.protocol.responses.PrepareGameResponse;
import sc.protocol.responses.RoomPacket;
import sc.shared.GameResult;
import sc.shared.SharedConfiguration;
import sc.shared.SlotDescriptor;

/**
 * This class is used to handle all communication with a server. It is used in a
 * client (e.g. the java simple client). It is also used to represent
 * observer-threads started by the server which connect to the server. The
 * server always has a {@link sc.server.network.Client} object for every
 * LobbyClient representing the client on the server-side.
 */
public final class LobbyClient extends XStreamClient implements IPollsHistory
{
	private static final Logger					logger					= LoggerFactory
			.getLogger(LobbyClient.class);
	private final List<String>					rooms					= new LinkedList<>();
	private final AsyncResultManager			asyncManager			= new AsyncResultManager();
	private final List<ILobbyClientListener>	listeners				= new LinkedList<>();
	private final List<IHistoryListener>		historyListeners		= new LinkedList<>();
	private final List<IAdministrativeListener>	administrativeListeners	= new LinkedList<>();

	public static final String					DEFAULT_HOST			= "localhost";

	public LobbyClient(XStream xStream) throws IOException
	{
		this(xStream, null);
	}

	public LobbyClient(XStream xStream, Collection<Class<?>> protocolClasses)
			throws IOException
	{
		this(xStream, protocolClasses, DEFAULT_HOST,
				SharedConfiguration.DEFAULT_PORT);
	}

	public LobbyClient(XStream xstream, Collection<Class<?>> protocolClasses,
			String host, int port) throws IOException
	{
		super(xstream, createTcpNetwork(host, port));
		LobbyProtocol.registerMessages(xstream);
		LobbyProtocol.registerAdditionalMessages(xstream, protocolClasses);
	}

	private static INetworkInterface createTcpNetwork(String host, int port)
			throws IOException
	{
		logger.info("Creating TCP Network for {}:{}", host, port);
		return new TcpNetwork(new Socket(host, port));
	}

	public List<String> getRooms()
	{
		return Collections.unmodifiableList(this.rooms);
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
				logger.info("Received game result");
				onGameOver(roomId, (GameResult) packet.getData());
			}
			else if (packet.getData() instanceof GamePausedEvent)
			{
				onGamePaused(roomId,
						((GamePausedEvent) packet.getData()).getNextPlayer());
			}
			else if (packet.getData() instanceof ErrorResponse)
			{
				logger.debug("Received error packet");
				onError(roomId, ((ErrorResponse) packet.getData()));
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
			this.rooms.add(roomId);
			onGameJoined(roomId);
		}
		else if (o instanceof LeftGameEvent)
		{
			String roomId = ((LeftGameEvent) o).getRoomId();
			this.rooms.remove(roomId);
			onGameLeft(roomId);
		}
		else if (o instanceof ErrorResponse)
		{
			ErrorResponse response = (ErrorResponse) o;

			onError(response.getMessage(), response);
		}
		else if (o instanceof ObservationResponse)
		{
			String roomId = ((ObservationResponse) o).getRoomId();

			onGameObserved(roomId);
		}
		else
		{
			onCustomObject(o);
		}
	}

	private void onGamePaused(String roomId, SimplePlayer nextPlayer)
	{
		for (IAdministrativeListener listener : this.administrativeListeners)
		{
			listener.onGamePaused(roomId, nextPlayer);
		}

		for (ILobbyClientListener listener : this.listeners)
		{
			listener.onGamePaused(roomId, nextPlayer);
		}
	}

	private void onGameOver(String roomId, GameResult data)
	{
		for (IHistoryListener listener : this.historyListeners)
		{
			listener.onGameOver(roomId, data);
		}

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

	private void onGameObserved(String roomId)
	{
		for (ILobbyClientListener listener : this.listeners)
		{
			listener.onGameObserved(roomId);
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
				logger.warn(
						"Couldn't invoke Handlers because OriginalRequest was null.");
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
		return blockingRequest(new PrepareGameRequest(gameType),
				PrepareGameResponse.class);
	}

	public RequestResult<PrepareGameResponse> prepareGameAndWait(
			String gameType, SlotDescriptor... descriptors)
			throws InterruptedException
	{
		return blockingRequest(new PrepareGameRequest(gameType, descriptors),
				PrepareGameResponse.class);
	}

	public RequestResult<PrepareGameResponse> prepareGameAndWait(
			PrepareGameRequest request) throws InterruptedException
	{
		return blockingRequest(request, PrepareGameResponse.class);
	}

	public void prepareGame(String gameType)
	{
		send(new PrepareGameRequest(gameType));
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

	protected void onError(String roomId, ErrorResponse error)
	{
		if (error.getOriginalRequest() != null)
		{
			logger.warn("The request {} caused the following error: {}",
					error.getOriginalRequest().getClass(), error.getMessage());
		}
		else
		{
			logger.warn("An error occured: {}", error.getMessage());
		}
		for (ILobbyClientListener listener : this.listeners)
		{
			listener.onError(roomId, error);
		}
		for (IHistoryListener listener : this.historyListeners)
		{
			listener.onGameError(roomId, error);
		}
	}

	public void sendMessageToRoom(String roomId, Object o)
	{
		send(new RoomPacket(roomId, o));
	}

	protected void onRoomMessage(String roomId, Object data)
	{
		for (ILobbyClientListener listener : this.listeners)
		{
			listener.onRoomMessage(roomId, data);
		}
	}

	/**
	 * used in server
	 * @param reservation
	 */
	public void joinPreparedGame(String reservation)
	{
		send(new JoinPreparedRoomRequest(reservation));
	}

	/**
	 * currently not used in server
	 */
	public void joinAnyGame(String gameType)
	{
		send(new JoinRoomRequest(gameType));
	}
	
	/**
	 * currently not used in server
	 * @param gameType
	 * @param roomId
	 */

	public void joinRoom(String gameType, String roomId)
	{
		send(new JoinRoomRequest(gameType, roomId));
	}

	/**
	 * used in server
	 * @param request
	 * @param response
	 * @param handler
	 */
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

				@Override
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
		IControllableGame result = new ControllingClient(this,
				handle.getRoomId());
		start();
		logger.debug("sending observation request with handle.roomId {}",
				handle.getRoomId());
		send(new ObservationRequest(handle.getRoomId(), ""));
		result.pause();
		return result;
	}

	public IControllableGame observe(PrepareGameResponse handle)
	{
		return observe(handle.getRoomId());
	}

	public IControllableGame observe(String roomId)
	{
		IControllableGame result = new ObservingClient(this, roomId);
		start();
		send(new ObservationRequest(roomId, ""));
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

	public void addListener(IAdministrativeListener listener)
	{
		this.administrativeListeners.add(listener);
	}

	public void removeListener(IAdministrativeListener listener)
	{
		this.administrativeListeners.remove(listener);
	}

	public void freeReservation(String reservation)
	{
		send(new FreeReservationRequest(reservation));
	}
}
