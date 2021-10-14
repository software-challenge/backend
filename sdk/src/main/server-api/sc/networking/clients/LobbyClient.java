package sc.networking.clients;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.IGameState;
import sc.player.IGameHandler;
import sc.player.IPlayerClient;
import sc.player.PlayerClient;
import sc.protocol.ProtocolPacket;
import sc.protocol.RemovedFromGame;
import sc.protocol.ResponsePacket;
import sc.protocol.requests.*;
import sc.protocol.responses.*;
import sc.protocol.room.*;
import sc.shared.GameResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class is used to handle all communication with a server.
 *
 * - It is used in a client (e.g. the java simple client).
 * - It is also used to represent observer-threads started by the server which connect to the server.
 *
 * The server always has a Client object for every LobbyClient representing the client on the server-side.
 */
public final class LobbyClient extends XStreamClient implements IPollsHistory {
  private static final Logger logger = LoggerFactory.getLogger(LobbyClient.class);
  private final List<ILobbyClientListener> listeners = new ArrayList<>();
  private final List<IHistoryListener> historyListeners = new ArrayList<>();

  private Function<RoomMessage, RoomMessage> player = null;
  /** Whether to preserve the connection when a game ends. */
  private boolean keepAlive = true;
  private Consumer<ResponsePacket> administrativeListener = null;
  private final Map<String, Consumer<ObservableRoomMessage>> roomObservers = new HashMap<>();

  public LobbyClient(String host, int port) throws IOException {
    super(createTcpNetwork(host, port));
  }

  /** Request authentication on server with a listener.
   * @return an AdminClient to send authorised requests */
  public AdminClient authenticate(String password, Consumer<ResponsePacket> consumer) {
    start();
    if(administrativeListener != null)
      logger.warn("Re-authentication replaces {}", administrativeListener);
    administrativeListener = consumer;
    send(new AuthenticateRequest(password));
    return new AdminClient(this);
  }

  /** Sets observer to observe messages in the given room.
   * Whether administrative messages are received depends on authentication,
   * which has to be done separately. */
  public void observeRoom(String roomId, Consumer<ObservableRoomMessage> observer) {
    roomObservers.put(roomId, observer);
  }

  /** Sets this client up to play a game utilizing the handler.
   * @param handler the game logic
   * @return a PlayerClient to join a room as Player. */
  public @NotNull IPlayerClient asPlayer(IGameHandler handler) {return asPlayer(handler, false);}

  /** Sets this client up to play games utilizing the handler.
   * @param handler the game logic
   * @param keepAlive whether the client should stay connected after a game ends
   * @return a PlayerClient to join a room as Player. */
  public @NotNull IPlayerClient asPlayer(IGameHandler handler, boolean keepAlive) {
    start();
    PlayerClient client = new PlayerClient(this, handler);
    player = client;
    this.keepAlive = keepAlive;
    return client;
  }

  @Override
  protected void onObject(ProtocolPacket message) {
    if(message instanceof ResponsePacket && administrativeListener != null)
      administrativeListener.accept((ResponsePacket) message);
    if (message instanceof RoomPacket) {
      RoomPacket packet = (RoomPacket) message;
      String roomId = packet.getRoomId();
      RoomMessage data = packet.getData();
     if(player != null) {
        // TODO run this in a background thread
        RoomMessage response = player.apply(data);
        if(response != null)
          sendMessageToRoom(roomId, response);
      }
      if(data instanceof ObservableRoomMessage) {
        roomObservers.getOrDefault(roomId, (m) -> {}).accept((ObservableRoomMessage) data);
        if (data instanceof MementoMessage) {
          onNewState(roomId, ((MementoMessage) data).getState());
        } else if (data instanceof GameResult) {
          onGameOver(roomId, (GameResult) data);
        } else if (data instanceof ErrorMessage) {
          ErrorMessage error = (ErrorMessage) data;
          logger.warn("{} in room {}", error.getLogMessage(), roomId);
          for (IHistoryListener listener : this.historyListeners) {
            listener.onGameError(roomId, error);
          }
        }
      } else {
        onRoomMessage(roomId, data);
      }
    } else if (message instanceof RemovedFromGame) {
      String roomId = ((RemovedFromGame) message).getRoomId();
      logger.info("Left {}", roomId);
      if(!keepAlive)
        stop();
    } else if (message instanceof GamePreparedResponse) {
      onGamePrepared((GamePreparedResponse) message);
    } else if (message instanceof JoinedRoomResponse) {
      onGameJoined(((JoinedRoomResponse) message).getRoomId());
    } else if (message instanceof RoomWasJoinedEvent) {
      onGameJoined(((RoomWasJoinedEvent) message).getRoomId());
    } else if (message instanceof ObservationResponse) {
      onGameObserved(((ObservationResponse) message).getRoomId());
    } else if (message instanceof ErrorPacket) {
      ErrorPacket error = (ErrorPacket) message;
      for (ILobbyClientListener listener : this.listeners) {
        listener.onError(error);
      }
    } else {
      onCustomObject(message);
    }
  }

  public void sendMessageToRoom(String roomId, RoomMessage o) {
    send(new RoomPacket(roomId, o));
  }

  // TODO The following functions are obsolete
  //  and will be removed once all tests are migrated to the new style

  private void onGameOver(String roomId, GameResult data) {
    logger.info("Received game result: {}", data);
    for (IHistoryListener listener : this.historyListeners) {
      listener.onGameOver(roomId, data);
    }

    for (ILobbyClientListener listener : this.listeners) {
      listener.onGameOver(roomId, data);
    }
  }

  private void onGameJoined(String roomId) {
    for (ILobbyClientListener listener : new ArrayList<>(this.listeners)) {
      listener.onGameJoined(roomId);
    }
  }

  private void onGameObserved(String roomId) {
    for (ILobbyClientListener listener : this.listeners) {
      listener.onGameObserved(roomId);
    }
  }

  protected void onGamePrepared(GamePreparedResponse response) {
    for (ILobbyClientListener listener : new ArrayList<>(this.listeners)) {
      listener.onGamePrepared(response);
    }
  }

  public void authenticate(String password) {
    send(new AuthenticateRequest(password));
  }

  protected void onCustomObject(Object o) {
    logger.warn("Couldn't process message {}.", o);
  }

  protected void onNewState(String roomId, IGameState state) {
    for (ILobbyClientListener listener : this.listeners) {
      listener.onNewState(roomId, state);
    }
    for (IHistoryListener listener : this.historyListeners) {
      listener.onNewState(roomId, state);
    }
  }

  protected void onRoomMessage(String roomId, RoomMessage data) {
    for (ILobbyClientListener listener : this.listeners) {
      listener.onRoomMessage(roomId, data);
    }
  }

  public void joinGameWithReservation(String reservation) {
    send(new JoinPreparedRoomRequest(reservation));
  }

  public void joinGameRoom(String roomId) {
    send(new JoinRoomRequest(roomId));
  }

  public void joinGame(String gameType) {
    send(new JoinGameRequest(gameType));
  }

  public void addListener(ILobbyClientListener listener) {
    this.listeners.add(listener);
  }

  public void removeListener(ILobbyClientListener listener) {
    this.listeners.remove(listener);
  }

  public ObservingClient observe(String roomId) {
    return observe(roomId, false);
  }

  public ObservingClient observe(String roomId, boolean isPaused) {
    ObservingClient observer = new ObservingClient(roomId, isPaused);
    addListener(observer);
    send(new ObservationRequest(roomId));
    return observer;
  }

  @Override
  public void addListener(IHistoryListener listener) {
    this.historyListeners.add(listener);
  }

  @Override
  public void removeListener(IHistoryListener listener) {
    this.historyListeners.remove(listener);
  }

}
