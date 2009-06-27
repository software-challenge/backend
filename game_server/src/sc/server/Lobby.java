package sc.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.IPlayer;
import sc.server.gaming.GameRoom;
import sc.server.gaming.GameRoomManager;
import sc.server.network.Client;
import sc.server.network.ClientManager;
import sc.server.network.IClientListener;
import sc.server.protocol.ILobbyRequest;
import sc.server.protocol.JoinPreparedRoomRequest;
import sc.server.protocol.JoinRoomRequest;

/**
 * The lobby will help clients find a open game or create new games to play with
 * another client.
 * 
 * @author mja
 * @author rra
 */
public class Lobby implements IClientManagerListener, IClientListener
{
	private Logger					logger			= LoggerFactory
															.getLogger(Lobby.class);
	private Map<IPlayer, Client>	players			= new HashMap<IPlayer, Client>();
	private GameRoomManager			gameManager		= new GameRoomManager();
	private ClientManager			clientManager	= new ClientManager();

	public Collection<GameRoom> listOpenGames()
	{
		return listOpenGames(null);
	}

	public Collection<GameRoom> listOpenGames(String pluginName)
	{
		return new ArrayList<GameRoom>(0);
	}

	public Client resolvePlayer(IPlayer player)
	{
		return this.players.get(player);
	}

	public void start()
	{
		gameManager.start();
		clientManager.addListener(this);
		clientManager.start();
	}

	@Override
	public void onClientConnected(Client client)
	{
		client.addClientListener(this);
	}

	@Override
	public void onClientDisconnected(Client source)
	{
		// I don't care
	}

	@Override
	public void onRequest(Client source, Object packet) throws RescueableClientException
	{
		if (packet instanceof ILobbyRequest)
		{
			if (packet instanceof JoinPreparedRoomRequest)
			{
				// TODO: implement
				gameManager.joinGame(source, 0);
			}
			else if (packet instanceof JoinRoomRequest)
			{
				gameManager.joinOrCreateGame(source, ((JoinRoomRequest)packet).getGameType());
			}
			else
			{
				logger.warn("Unhandled Packet of type: " + packet.getClass());
			}
		}
	}

	public void close()
	{
		clientManager.close();
		gameManager.close();
	}

	public GameRoomManager getGameManager()
	{
		return this.gameManager;
	}
}
