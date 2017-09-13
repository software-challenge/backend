package sc.networking.clients;

import java.io.IOException;
import java.net.Socket;
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
import sc.protocol.requests.JoinPreparedRoomRequest;
import sc.protocol.requests.JoinRoomRequest;
import sc.protocol.requests.ObservationRequest;
import sc.protocol.requests.PrepareGameRequest;
import sc.protocol.responses.*;
import sc.protocol.responses.PrepareGameProtocolMessage;
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
public final class LobbyClient extends XStreamClient implements IPollsHistory {
  private static final Logger logger = LoggerFactory
          .getLogger(LobbyClient.class);
  private final List<String> rooms = new LinkedList<>();
  private final AsyncResultManager asyncManager = new AsyncResultManager();
  private final List<ILobbyClientListener> listeners = new LinkedList<>();
  private final List<IHistoryListener> historyListeners = new LinkedList<>();
  private final List<IAdministrativeListener> administrativeListeners = new LinkedList<>();

  public static final String DEFAULT_HOST = "localhost";

  public LobbyClient(XStream xStream) throws IOException {
    this(xStream, null);
  }

  public LobbyClient(XStream xStream, Collection<Class<?>> protocolClasses)
          throws IOException {
    this(xStream, protocolClasses, DEFAULT_HOST,
            SharedConfiguration.DEFAULT_PORT);
  }

  public LobbyClient(XStream xstream, Collection<Class<?>> protocolClasses,
                     String host, int port) throws IOException {
    super(xstream, createTcpNetwork(host, port));
    LobbyProtocol.registerMessages(xstream);
    LobbyProtocol.registerAdditionalMessages(xstream, protocolClasses);
  }

  private static INetworkInterface createTcpNetwork(String host, int port)
          throws IOException {
    logger.info("Creating TCP Network for {}:{}", host, port);
    return new TcpNetwork(new Socket(host, port));
  }

  public List<String> getRooms() {
    return Collections.unmodifiableList(this.rooms);
  }

  @Override
  protected final void onObject(ProtocolMessage o) {
    if (o == null) {
      logger.warn("Received null object.");
      return;
    }

    invokeHandlers(o);

    if (o instanceof RoomPacket) {
      RoomPacket packet = (RoomPacket) o;
      String roomId = packet.getRoomId();
      if (packet.getData() instanceof MementoPacket) {
        MementoPacket statePacket = (MementoPacket) packet.getData();
        onNewState(roomId, statePacket.getState());
      } else if (packet.getData() instanceof GameResult) {
        logger.info("Received game result");
        onGameOver(roomId, (GameResult) packet.getData());
      } else if (packet.getData() instanceof GamePausedEvent) {
        onGamePaused(roomId,
                ((GamePausedEvent) packet.getData()).getNextPlayer());
      } else if (packet.getData() instanceof ProtocolErrorMessage) {
        logger.debug("Received error packet");
        onError(roomId, ((ProtocolErrorMessage) packet.getData()));
      } else {
        onRoomMessage(roomId, packet.getData());
      }
    } else if (o instanceof PrepareGameProtocolMessage) {
      PrepareGameProtocolMessage preparation = (PrepareGameProtocolMessage) o;
      onGamePrepared(preparation);
    } else if (o instanceof JoinGameProtocolMessage) {
      String roomId = ((JoinGameProtocolMessage) o).getRoomId();
      this.rooms.add(roomId);
      onGameJoined(roomId);
    } else if (o instanceof LeftGameEvent) {
      String roomId = ((LeftGameEvent) o).getRoomId();
      this.rooms.remove(roomId);
      onGameLeft(roomId);
    } else if (o instanceof ProtocolErrorMessage) {
      ProtocolErrorMessage response = (ProtocolErrorMessage) o;

      onError(response.getMessage(), response);
    } else if (o instanceof ObservationProtocolMessage) {
      String roomId = ((ObservationProtocolMessage) o).getRoomId();

      onGameObserved(roomId);
    } else {
      onCustomObject(o);
    }
  }

  private void onGamePaused(String roomId, SimplePlayer nextPlayer) {
    for (IAdministrativeListener listener : this.administrativeListeners) {
      listener.onGamePaused(roomId, nextPlayer);
    }

    for (ILobbyClientListener listener : this.listeners) {
      listener.onGamePaused(roomId, nextPlayer);
    }
  }

  private void onGameOver(String roomId, GameResult data) {
    for (IHistoryListener listener : this.historyListeners) {
      listener.onGameOver(roomId, data);
    }

    for (ILobbyClientListener listener : this.listeners) {
      listener.onGameOver(roomId, data);
    }
  }

  private void onGameLeft(String roomId) {
    for (ILobbyClientListener listener : this.listeners) {
      listener.onGameLeft(roomId);
    }
  }

  private void onGameJoined(String roomId) {
    for (ILobbyClientListener listener : this.listeners) {
      listener.onGameJoined(roomId);
    }
  }

  private void onGameObserved(String roomId) {
    for (ILobbyClientListener listener : this.listeners) {
      listener.onGameObserved(roomId);
    }
  }

  private void invokeHandlers(ProtocolMessage o) {
    if (o == null) {
      throw new IllegalArgumentException("o was null");
    }
    this.asyncManager.invokeHandlers(o);
  }

  protected void onGamePrepared(PrepareGameProtocolMessage response) {
    for (ILobbyClientListener listener : this.listeners) {
      listener.onGamePrepared(response);
    }
  }

  public void authenticate(String password) {
    send(new AuthenticateRequest(password));
  }

  public RequestResult<PrepareGameProtocolMessage> prepareGameAndWait(
          String gameType, int playerCount) throws InterruptedException {
    return blockingRequest(new PrepareGameRequest(gameType),
            PrepareGameProtocolMessage.class);
  }

  public RequestResult<PrepareGameProtocolMessage> prepareGameAndWait(
          String gameType, SlotDescriptor descriptor1, SlotDescriptor descriptor2)
          throws InterruptedException {
    return blockingRequest(new PrepareGameRequest(gameType, descriptor1, descriptor2),
            PrepareGameProtocolMessage.class);
  }

  public RequestResult<PrepareGameProtocolMessage> prepareGameAndWait(
          PrepareGameRequest request) throws InterruptedException {
    return blockingRequest(request, PrepareGameProtocolMessage.class);
  }

  public void prepareGame(String gameType) {
    send(new PrepareGameRequest(gameType));
  }

  public void prepareGame(String gameType, boolean startPaused) {
    send(new PrepareGameRequest(
            gameType,
            new SlotDescriptor("player1", false, startPaused),
            new SlotDescriptor("player2", false, startPaused))
    );
  }

  protected void onCustomObject(Object o) {
    logger.warn("Couldn't process message {}.", o);
  }

  protected void onNewState(String roomId, Object state) {
    for (ILobbyClientListener listener : this.listeners) {
      listener.onNewState(roomId, state);
    }

    for (IHistoryListener listener : this.historyListeners) {
      listener.onNewState(roomId, state);
    }
  }

  protected void onError(String roomId, ProtocolErrorMessage error) {
    if (error.getOriginalRequest() != null) {
      logger.warn("The request {} caused the following error: {}",
              error.getOriginalRequest().getClass(), error.getMessage());
    } else {
      logger.warn("An error occured: {}", error.getMessage());
    }
    for (ILobbyClientListener listener : this.listeners) {
      listener.onError(roomId, error);
    }
    for (IHistoryListener listener : this.historyListeners) {
      listener.onGameError(roomId, error);
    }
  }

  public void sendMessageToRoom(String roomId, ProtocolMessage o) {
    send(new RoomPacket(roomId, o));
  }

  protected void onRoomMessage(String roomId, ProtocolMessage data) {
    for (ILobbyClientListener listener : this.listeners) {
      listener.onRoomMessage(roomId, data);
    }
  }

  /**
   * used in server
   *
   * @param reservation
   */
  public void joinPreparedGame(String reservation) {
    send(new JoinPreparedRoomRequest(reservation));
  }

  /**
   * currently not used in server
   */
  public void joinRoomRequest(String gameType) {
    send(new JoinRoomRequest(gameType));
  }

  /**
   * used in server
   *
   * @param request
   * @param response
   * @param handler
   */
  protected void request(ProtocolMessage request, Class<? extends ProtocolMessage> response,
                         IRequestResult handler) {
    this.asyncManager.addHandler(response, handler);
    send(request);
  }

  protected RequestResult blockingRequest(ProtocolMessage request,
                                                 Class<? extends ProtocolMessage> response) throws InterruptedException {
    final RequestResult requestResult = new RequestResult();
    final Object beacon = new Object();
    synchronized (beacon) {
      IRequestResult blockingHandler = new IRequestResult() {

        @Override
        public void handleError(ProtocolErrorMessage e) {
          requestResult.setError(e);
          notifySemaphore();
        }

        @Override
        public void operate(ProtocolMessage result) {
          requestResult.setResult(result);
          notifySemaphore();
        }

        private void notifySemaphore() {
          synchronized (beacon) {
            beacon.notify();
          }
        }
      };
      request(request, response, blockingHandler);
      beacon.wait();
    }

    return requestResult;
  }

  public void addListener(ILobbyClientListener listener) {
    this.listeners.add(listener);
  }

  public void removeListener(ILobbyClientListener listener) {
    this.listeners.remove(listener);
  }

  public IControllableGame observeAndControl(PrepareGameProtocolMessage handle) {
    IControllableGame result = new ControllingClient(this,
            handle.getRoomId());
    start();
    logger.debug("sending observation request with handle.roomId {}",
            handle.getRoomId());
    send(new ObservationRequest(handle.getRoomId()));
    result.pause();
    return result;
  }

  public IControllableGame observe(PrepareGameProtocolMessage handle) {
    return observe(handle.getRoomId());
  }

  public IControllableGame observe(String roomId) {
    IControllableGame result = new ObservingClient(this, roomId);
    start();
    send(new ObservationRequest(roomId));
    return result;
  }

  @Override
  public void addListener(IHistoryListener listener) {
    this.historyListeners.add(listener);
  }

  @Override
  public void removeListener(IHistoryListener listener) {
    this.historyListeners.remove(listener);
  }

  public void addListener(IAdministrativeListener listener) {
    this.administrativeListeners.add(listener);
  }

  public void removeListener(IAdministrativeListener listener) {
    this.administrativeListeners.remove(listener);
  }

  public void freeReservation(String reservation) {
    send(new FreeReservationRequest(reservation));
  }
}
