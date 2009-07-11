package sc.plugin2010;

import java.io.IOException;

import sc.framework.plugins.protocol.MoveRequest;
import sc.protocol.ErrorResponse;
import sc.protocol.ILobbyClientListener;
import sc.protocol.LobbyClient;

import com.thoughtworks.xstream.XStream;

/**
 * Der Client für das Hase- und Igel Plugin.
 * 
 * @author rra
 * @since Jul 5, 2009
 * 
 */
public class Client implements ILobbyClientListener
{
	private IGameHandler	handler;
	private LobbyClient		client;
	private String			gameType;
	// current id to identifiy the client instance internal
	private EPlayerId		id;
	// the current room in which the player is
	private String			roomId;

	public Client(XStream xstream, String host, int port, EPlayerId id)
			throws IOException
	{
		this.gameType = "";
		client = new LobbyClient(xstream, host, port);
		client.addListener(this);
		this.id = id;
	}

	public void setHandler(IGameHandler handler)
	{
		this.handler = handler;
	}

	public IGameHandler getHandler()
	{
		return handler;
	}

	@Override
	public void onRoomMessage(String roomId, Object data)
	{
		if (data instanceof BoardUpdated)
		{
			handler.onUpdate((BoardUpdated) data);
		}
		else if (data instanceof PlayerUpdated)
		{
			handler.onUpdate((PlayerUpdated) data);
		}
		else if (data instanceof MoveRequest)
		{
			handler.onRequestAction();
		}

		this.roomId = roomId;
	}

	// TODO call it
	public void sendMove(Move move)
	{
		client.sendMessageToRoom(roomId, move);
	}

	@Override
	public void onError(ErrorResponse response)
	{
		System.err.println(response.getMessage());
	}

	@Override
	public void onNewState(String roomId, Object state)
	{
		// TODO Auto-generated method stub
	}

	public void joinAnyGame()
	{
		client.joinAnyGame(gameType);
	}

	/**
	 * @return
	 */
	public String getGameType()
	{
		return gameType;
	}

	public EPlayerId getID()
	{
		return id;
	}

	public void prepareGame(int playerCount)
	{
		client.prepareGame(gameType, playerCount);
	}
}
