package sc.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.exceptions.RescuableClientException;
import sc.protocol.requests.*;
import sc.protocol.responses.*;
import sc.server.gaming.*;
import sc.server.network.Client;
import sc.server.network.ClientManager;
import sc.server.network.IClientListener;
import sc.server.network.IClientRole;
import sc.server.network.PacketCallback;
import sc.shared.InvalidGameStateException;
import sc.shared.Score;
import sc.shared.SlotDescriptor;

/**
 * The lobby will help clients find a open game or create new games to play with
 * another client.
 */
public class Lobby implements IClientListener {
  private final Logger logger = LoggerFactory
          .getLogger(Lobby.class);
  private final GameRoomManager gameManager;
  private final ClientManager clientManager;

  public Lobby() {
    this.gameManager = new GameRoomManager();
    this.clientManager = new ClientManager(this);
  }

  /**
   * Starts the ClientManager in it's own daemon thread. This method should be used only once.
   * ClientManager starts clientListener.
   * clientListener starts SocketListener on defined port to watch for new connecting clients.
   */
  public void start() throws IOException {
    this.clientManager.start();
  }

  /**
   * Add lobby as listener to client.
   * Prepare client for send and receive.
   *
   * @param client connected XStreamClient
   */
  public void onClientConnected(Client client) {
    client.addClientListener(this);
    client.start();
  }

  @Override
  public void onClientDisconnected(Client source) {
    this.logger.info("{} disconnected.", source);
    source.removeClientListener(this);
  }

  /**
   * handle requests or moves of clients
   */
  @Override
  public void onRequest(Client source, PacketCallback callback)
          throws RescuableClientException, InvalidGameStateException {
    Object packet = callback.getPacket();

    if (packet instanceof ILobbyRequest) {
      if (packet instanceof JoinPreparedRoomRequest) {
        ReservationManager
                .redeemReservationCode(source,
                        ((JoinPreparedRoomRequest) packet)
                                .getReservationCode());
      } else if (packet instanceof JoinRoomRequest) {
        GameRoomMessage gameRoomMessage = this.gameManager.joinOrCreateGame(source,
                ((JoinRoomRequest) packet).getGameType());
        // null is returned if join was unsuccessful
        if (gameRoomMessage != null) {
          for (Client admin :
                  clientManager.getClients()) {
            if (admin.isAdministrator()) {
              admin.send(gameRoomMessage);
            }
          }
        }
      } else if (packet instanceof AuthenticateRequest) {
        source.authenticate(((AuthenticateRequest) packet)
                .getPassword());
      } else if (packet instanceof PrepareGameRequest) {
        if (source.isAdministrator()) {
          PrepareGameRequest prepared = (PrepareGameRequest) packet;
          source.send(this.gameManager.prepareGame(prepared));
        }
      } else if (packet instanceof FreeReservationRequest) {
        if (source.isAdministrator()) {
          FreeReservationRequest request = (FreeReservationRequest) packet;
          ReservationManager.freeReservation(request.getReservation());
        }
      } else if (packet instanceof RoomPacket)  // i.e. new move
      {
        RoomPacket casted = (RoomPacket) packet;
        GameRoom room = this.gameManager.findRoom(casted.getRoomId());
        room.onEvent(source, casted.getData());
      } else if (packet instanceof ObservationRequest) {
        if (source.isAdministrator()) {
          ObservationRequest observe = (ObservationRequest) packet;
          GameRoom room = this.gameManager.findRoom(observe.getRoomId());
          room.addObserver(source);
        }
      } else if (packet instanceof PauseGameRequest) {
        if (source.isAdministrator()) {
          PauseGameRequest pause = (PauseGameRequest) packet;
          try {
            GameRoom room = this.gameManager.findRoom(pause.roomId);
            room.pause(pause.pause);
          } catch (RescuableClientException e) {
            this.logger.error("Got exception on pause: {}", e);
          }
        }
      } else if (packet instanceof ControlTimeoutRequest) {
        if (source.isAdministrator()) {
          ControlTimeoutRequest timeout = (ControlTimeoutRequest) packet;

          GameRoom room = this.gameManager.findRoom(timeout.roomId);

          PlayerSlot slot = room.getSlots().get(timeout.slot);
          slot.getRole().getPlayer().setCanTimeout(timeout.activate);

        }
      } else if (packet instanceof StepRequest) {
        // It is not checked whether there is a prior pending StepRequest
        if (source.isAdministrator()) {
          StepRequest stepRequest = (StepRequest) packet;
          GameRoom room = this.gameManager.findRoom(stepRequest.roomId);
          room.step(stepRequest.forced);
        }
      } else if (packet instanceof CancelRequest) {
        if (source.isAdministrator()) {
          CancelRequest cancel = (CancelRequest) packet;
          GameRoom room = this.gameManager.findRoom(cancel.roomId);
          room.cancel();
          // TODO check whether all clients receive game over message
          this.gameManager.getGames().remove(room);
        }
      } else if (packet instanceof TestModeRequest) {
        if (source.isAdministrator()) {
          boolean testMode = ((TestModeRequest) packet).testMode;
          logger.info("Test mode is set to {}", testMode);
          Configuration.set(Configuration.TEST_MODE, Boolean.toString(testMode));
          source.send(new TestModeMessage(testMode));
        }
      } else if (packet instanceof GetScoreForPlayerRequest) {
        if (source.isAdministrator()) {
          String displayName = ((GetScoreForPlayerRequest) packet).getDisplayName();
          Score score = getScoreOfPlayer(displayName);
          if (score == null)
            throw new IllegalArgumentException("Score for \"" + displayName + "\" could not be found!");
          logger.debug("Sending score of player \"{}\"", displayName);
          source.send(new PlayerScorePacket(score));
        }
      } else {
        throw new RescuableClientException("Unhandled Packet of type: " + packet.getClass());
      }

      callback.setProcessed();
    }
  }

  private Score getScoreOfPlayer(String displayName) {
    for (Score score : this.gameManager.getScores()) {
      if (score.getDisplayName().equals(displayName)) {
        return score;
      }
    }
    return null;
  }

  public void close() {
    this.clientManager.close();
  }

  public GameRoomManager getGameManager() {
    return this.gameManager;
  }

  public ClientManager getClientManager() {
    return this.clientManager;
  }

  @Override
  public void onError(Client source, ProtocolErrorMessage errorPacket) {
    for (IClientRole role : source.getRoles()) {
      if (role.getClass() == PlayerRole.class) {
        ((PlayerRole) role).getPlayerSlot().getRoom().onClientError(errorPacket);
      }
    }
  }
}
