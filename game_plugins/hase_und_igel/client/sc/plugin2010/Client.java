package sc.plugin2010;

import java.io.IOException;
import java.util.Collection;

import sc.framework.plugins.protocol.MoveRequest;
import sc.protocol.ErrorResponse;
import sc.protocol.LobbyClient;

import com.thoughtworks.xstream.XStream;

/**
 * Der Client für das Hase- und Igel Plugin.
 * 
 * @author rra
 * @since Jul 5, 2009
 * 
 */
public class Client extends LobbyClient
{
	private IGameHandler	handler;
	private String			gameType;
	// current id to identifiy the client instance internal
	private EPlayerId		id;
	// the current room in which the player is
	private String			roomId;

	public Client(String gameType, XStream xstream, String host, int port,
			EPlayerId id) throws IOException
	{
		super(gameType, xstream, host, port);
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
	protected void onRoomMessage(String roomId, Object data)
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

	@Override
	protected Collection<Class<? extends Object>> getProtocolClasses()
	{
		return null;
	}

	// TODO call it
	public void sendMove(Move move)
	{
		sendMessageToRoom(roomId, move);
	}

	@Override
	protected void onError(ErrorResponse response)
	{
		System.err.println(response.getMessage());
	}

	@Override
	protected void onNewState(String roomId, Object state)
	{
		// TODO Auto-generated method stub
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
}
