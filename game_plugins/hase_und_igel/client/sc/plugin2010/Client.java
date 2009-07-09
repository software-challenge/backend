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
	// Die Strategie
	private IGameHandler	handler;

	public Client(String gameType, XStream xstream, String host, int port)
			throws IOException
	{
		super(gameType, xstream, host, port);
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
			sendMessageToRoom(roomId, handler.onAction());
		}
	}

	@Override
	protected Collection<Class<? extends Object>> getProtocolClasses()
	{
		return null;
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
}
