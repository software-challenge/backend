package sc.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.exceptions.RescueableClientException;
import sc.protocol.requests.AuthenticateRequest;
import sc.protocol.requests.CancelRequest;
import sc.protocol.requests.FreeReservationRequest;
import sc.protocol.requests.ILobbyRequest;
import sc.protocol.requests.JoinPreparedRoomRequest;
import sc.protocol.requests.JoinRoomRequest;
import sc.protocol.requests.ObservationRequest;
import sc.protocol.requests.PauseGameRequest;
import sc.protocol.requests.PrepareGameRequest;
import sc.protocol.requests.StepRequest;
import sc.protocol.responses.RoomPacket;
import sc.server.gaming.GameRoom;
import sc.server.gaming.GameRoomManager;
import sc.server.gaming.PlayerRole;
import sc.server.gaming.ReservationManager;
import sc.server.network.Client;
import sc.server.network.ClientManager;
import sc.server.network.IClientListener;
import sc.server.network.IClientRole;
import sc.server.network.PacketCallback;

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
	private final GameRoomManager	gameManager		= new GameRoomManager();
	private final ClientManager		clientManager	= new ClientManager();

	public Lobby()
	{
		this.clientManager.addListener(this);
	}

	public void start() throws IOException
	{
		this.gameManager.start();
		this.clientManager.start();
	}

	public void onClientConnected(Client client)
	{
		client.addClientListener(this);
		client.start();
	}

	@Override
	public void onClientDisconnected(Client source)
	{
		/*final Client client = source;
		new Thread(new Runnable() {
			@Override
			public void run()
			{
				synchronized(client) {
					client.removeClientListener(Lobby.this);
				}
			}
		}).start();*/
		this.logger.info("{} disconnected.", source);
		source.removeClientListener(this);
	}

	@Override
	public void onRequest(Client source, PacketCallback callback)
			throws RescueableClientException
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
				this.gameManager.joinOrCreateGame(source,
						((JoinRoomRequest) packet).getGameType());
			}
			else if (packet instanceof AuthenticateRequest)
			{
				source.authenticate(((AuthenticateRequest) packet)
						.getPassword());
			}
			else if (packet instanceof PrepareGameRequest)
			{
				PrepareGameRequest prepared = (PrepareGameRequest) packet;
				source.send(this.gameManager.prepareGame(prepared));
				/*source.send(this.gameManager.prepareGame(
						prepared.getGameType(), prepared.getPlayerCount(),
						prepared.getSlotDescriptors()));*/
			}
			else if (packet instanceof FreeReservationRequest)
			{
				FreeReservationRequest request = (FreeReservationRequest) packet;
				ReservationManager.freeReservation(request.getReservation());
			}
			else if (packet instanceof RoomPacket)	// e.g. new move
			{
				RoomPacket casted = (RoomPacket) packet;
				GameRoom room = this.gameManager.findRoom(casted.getRoomId());
				room.onEvent(source, casted.getData());
			}
			else if (packet instanceof ObservationRequest)
			{
				// TODO: check permissions
				ObservationRequest observe = (ObservationRequest) packet;
				GameRoom room = this.gameManager.findRoom(observe.getRoomId());
				room.addObserver(source);
			}
			else if (packet instanceof PauseGameRequest)
			{
				PauseGameRequest pause = (PauseGameRequest) packet;
				try {
					GameRoom room = this.gameManager.findRoom(pause.roomId);
					room.pause(pause.pause);
				} catch (RescueableClientException e) {
					this.logger.error("Got exception on pause: {}", e);
				}
			}
			else if (packet instanceof StepRequest)
			{
				StepRequest pause = (StepRequest) packet;
				GameRoom room = this.gameManager.findRoom(pause.roomId);
				room.step(pause.forced);
			}
			else if (packet instanceof CancelRequest)
			{
				CancelRequest cancel = (CancelRequest) packet;
				GameRoom room = this.gameManager.findRoom(cancel.roomId);
				room.cancel();
			}
			else
			{
				throw new RescueableClientException(
						"Unhandled Packet of type: " + packet.getClass());
			}

			callback.setProcessed();
		}
	}

	public void close()
	{
		this.clientManager.close();
		this.gameManager.close();
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
	public void onError(Client source, Object errorPacket)
	{
		for (IClientRole role : source.getRoles())
		{
			if (role.getClass() == PlayerRole.class) {
				((PlayerRole)role).getPlayerSlot().getRoom().onClientError(source, errorPacket);
			}
		}
	}
}
