package sc.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.exceptions.RescuableClientException;
import sc.protocol.requests.*;
import sc.protocol.responses.*;
import sc.server.gaming.GameRoom;
import sc.server.gaming.GameRoomManager;
import sc.server.gaming.PlayerRole;
import sc.server.gaming.ReservationManager;
import sc.server.network.Client;
import sc.server.network.ClientManager;
import sc.server.network.IClientListener;
import sc.server.network.IClientRole;
import sc.server.network.PacketCallback;
import sc.shared.Score;

/**
 * The lobby will help clients find a open game or create new games to play with
 * another client.
 *
 * @author mja
 * @author rra
 */
public class Lobby implements IClientListener
{
	private final Logger			logger			= LoggerFactory
															.getLogger(Lobby.class);
	private final GameRoomManager	gameManager;		
	private final ClientManager		clientManager;

	public Lobby()
	{
		this.gameManager = new GameRoomManager();
		this.clientManager = new ClientManager(this);
	}

	/**
	 * Starts the ClientManager in it's own daemon thread. This method should be used only once.
	 * ClientManager starts clientListener.
	 * clientListener starts SocketListener on defined port to watch for new connecting clients.
	 */
	public void start() throws IOException
	{
		this.clientManager.start();
	}

	/**
	 * Add lobby as listener to client. 
	 * Prepare client for send and receive.
	 * @param client connected XStreamClient
	 */
	public void onClientConnected(Client client)
	{
		client.addClientListener(this);
		client.start();
	}

	@Override
	public void onClientDisconnected(Client source)
	{
		this.logger.info("{} disconnected.", source);
		source.removeClientListener(this);
	}

	/**
	 * handle requests or moves of clients
	 */
	@Override
	public void onRequest(Client source, PacketCallback callback)
			throws RescuableClientException
	{
		Object packet = callback.getPacket();

		if (packet instanceof ILobbyRequest)
		{
			if (packet instanceof JoinPreparedRoomRequest)
			{
				ReservationManager
						.redeemReservationCode(source,
								((JoinPreparedRoomRequest) packet)
										.getReservationCode());
			}
			else if (packet instanceof JoinRoomRequest)
			{
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
			}
			else if (packet instanceof AuthenticateRequest)
			{
				source.authenticate(((AuthenticateRequest) packet)
						.getPassword());
			}
			else if (packet instanceof PrepareGameRequest)
			{
				if (source.isAdministrator()) {
					PrepareGameRequest prepared = (PrepareGameRequest) packet;
					source.send(this.gameManager.prepareGame(prepared));
				}
			}
			else if (packet instanceof FreeReservationRequest) {
        if (source.isAdministrator()) {
          FreeReservationRequest request = (FreeReservationRequest) packet;
          ReservationManager.freeReservation(request.getReservation());
        }
      }
			else if (packet instanceof RoomPacket)	// i.e. new move
			{
				RoomPacket casted = (RoomPacket) packet;
				GameRoom room = this.gameManager.findRoom(casted.getRoomId());
				room.onEvent(source, casted.getData());
			}
			else if (packet instanceof ObservationRequest)
			{
				if (source.isAdministrator()) {
          ObservationRequest observe = (ObservationRequest) packet;
          GameRoom room = this.gameManager.findRoom(observe.getRoomId());
          room.addObserver(source);
        }
			}
			else if (packet instanceof PauseGameRequest)
			{
			  if (source.isAdministrator()) {
          PauseGameRequest pause = (PauseGameRequest) packet;
          try {
            GameRoom room = this.gameManager.findRoom(pause.roomId);
            room.pause(pause.pause);
          } catch (RescuableClientException e) {
            this.logger.error("Got exception on pause: {}", e);
          }
        }
			}
			else if (packet instanceof StepRequest)
			{
			  if (source.isAdministrator()) {
          StepRequest pause = (StepRequest) packet;
          GameRoom room = this.gameManager.findRoom(pause.roomId);
          room.step(pause.forced);
        }
			}
			else if (packet instanceof CancelRequest)
			{
			  if (source.isAdministrator()) {
          CancelRequest cancel = (CancelRequest) packet;
          GameRoom room = this.gameManager.findRoom(cancel.roomId);
          room.cancel();
        }
			}
			else if (packet instanceof TestModeRequest) {
			  if (source.isAdministrator()) {
          boolean testMode = ((TestModeRequest) packet).testMode;
          logger.info("Test mode is set to {}", testMode);
          Configuration.set(Configuration.TEST_MODE, new Boolean(testMode).toString());
          source.send(new TestModeMessage(testMode));
        }
      }
      else if (packet instanceof GetScoreForPlayerRequest) {
			  if (source.isAdministrator()) {
          String displayName = ((GetScoreForPlayerRequest) packet).getDisplayName();
          logger.info("Trying to return score of player {}", displayName);
          source.send(new PlayerScorePacket(getScoreOfPlayer(displayName)));
        }
      }
			else
			{
				throw new RescuableClientException(
						"Unhandled Packet of type: " + packet.getClass());
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

  public void close()
	{
		this.clientManager.close();
	}

	public GameRoomManager getGameManager()
	{
		return this.gameManager;
	}

	public ClientManager getClientManager()
	{
		return this.clientManager;
	}

	@Override
	public void onError(Client source, ProtocolErrorMessage errorPacket)
	{
		for (IClientRole role : source.getRoles())
		{
			if (role.getClass() == PlayerRole.class) {
				((PlayerRole)role).getPlayerSlot().getRoom().onClientError(errorPacket);
			}
		}
	}
}
