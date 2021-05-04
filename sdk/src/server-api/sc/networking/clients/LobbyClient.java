package sc.networking.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.IGameState;
import sc.framework.plugins.Player;
import sc.protocol.RemovedFromGame;
import sc.protocol.ProtocolPacket;
import sc.protocol.requests.*;
import sc.protocol.responses.*;
import sc.protocol.room.*;
import sc.shared.GameResult;
import sc.shared.SlotDescriptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
  private final List<IAdministrativeListener> administrativeListeners = new ArrayList<>();

  public LobbyClient(String host, int port) throws IOException {
    super(createTcpNetwork(host, port));
  }

  @Override
  protected final void onObject(ProtocolPacket message) {
    if (message instanceof RoomPacket) {
      RoomPacket packet = (RoomPacket) message;
      String roomId = packet.getRoomId();
      RoomMessage data = packet.getData();
      if (data instanceof MementoMessage) {
        onNewState(roomId, ((MementoMessage) data).getState());
      } else if (data instanceof GameResult) {
        onGameOver(roomId, (GameResult) data);
      } else if (data instanceof GamePaused) {
        onGamePaused(roomId, ((GamePaused) data).getNextPlayer());
      } else if (data instanceof ErrorMessage) {
        ErrorMessage error = (ErrorMessage) data;
        logger.warn("{} in room {}", error.getLogMessage(), roomId);
        for (IHistoryListener listener : this.historyListeners) {
          listener.onGameError(roomId, error);
        }
      } else {
        onRoomMessage(roomId, data);
      }
    } else if (message instanceof GamePreparedResponse) {
      onGamePrepared((GamePreparedResponse) message);
    } else if (message instanceof JoinedRoomResponse) {
      onGameJoined(((JoinedRoomResponse) message).getRoomId());
    } else if (message instanceof RoomWasJoinedEvent) {
      onGameJoined(((RoomWasJoinedEvent) message).getRoomId());
    } else if (message instanceof RemovedFromGame) {
      onGameLeft(((RemovedFromGame) message).getRoomId());
    } else if (message instanceof ObservationResponse) {
      onGameObserved(((ObservationResponse) message).getRoomId());
    } else if (message instanceof TestModeResponse) {
      boolean testMode = (((TestModeResponse) message).getTestMode());
      logger.info("TestMode was set to {} ", testMode);
    } else if (message instanceof ErrorPacket) {
      ErrorPacket error = (ErrorPacket) message;
      for (ILobbyClientListener listener : this.listeners) {
        listener.onError(error);
      }
    } else {
      onCustomObject(message);
    }
  }

  private void onGamePaused(String roomId, Player nextPlayer) {
    for (IAdministrativeListener listener : this.administrativeListeners) {
      listener.onGamePaused(roomId, nextPlayer);
    }

    for (ILobbyClientListener listener : this.listeners) {
      listener.onGamePaused(roomId, nextPlayer);
    }
  }

  private void onGameOver(String roomId, GameResult data) {
    logger.info("Received game result: {}", data);
    for (IHistoryListener listener : this.historyListeners) {
      listener.onGameOver(roomId, data);
    }

    for (ILobbyClientListener listener : this.listeners) {
      listener.onGameOver(roomId, data);
    }
  }

  private void onGameLeft(String roomId) {
    logger.info("Received RemovedFromGame");
    for (ILobbyClientListener listener : this.listeners) {
      listener.onGameLeft(roomId);
    }
    logger.info("Left {}", roomId);
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

  public void sendMessageToRoom(String roomId, RoomMessage o) {
    send(new RoomPacket(roomId, o));
  }

  public void joinPreparedGame(String reservation) {
    send(new JoinPreparedRoomRequest(reservation));
  }

  public void joinRoomRequest(String gameType) {
    send(new JoinRoomRequest(gameType));
  }

  public void addListener(ILobbyClientListener listener) {
    this.listeners.add(listener);
  }

  public void removeListener(ILobbyClientListener listener) {
    this.listeners.remove(listener);
  }

  /** Takes control of the game in the given room and pauses it. */
  public IControllableGame observeAndControl(String roomId) {
    final IControllableGame controller = observeAndControl(roomId, true);
    controller.pause();
    return controller;
  }

  /** Takes control of the game in the given room.
   * @param isPaused whether the game to observe is already paused. */
  public IControllableGame observeAndControl(String roomId, boolean isPaused) {
    ControllingClient controller = new ControllingClient(this, roomId, isPaused);
    addListener((IAdministrativeListener) controller);
    addListener((IHistoryListener) controller);
    requestObservation(roomId);
    return controller;
  }

  public ObservingClient observe(String roomId) {
    return observe(roomId, false);
  }

  public ObservingClient observe(String roomId, boolean isPaused) {
    ObservingClient observer = new ObservingClient(roomId, isPaused);
    addListener(observer);
    requestObservation(roomId);
    return observer;
  }

  private void requestObservation(String roomId) {
    start();
    logger.debug("Sending observation request for roomId: {}", roomId);
    send(new ObservationRequest(roomId));
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

}
