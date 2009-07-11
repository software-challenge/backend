package sc.sample.client;

import java.io.IOException;
import java.util.Collection;

import sc.protocol.ErrorResponse;
import sc.protocol.ILobbyClientListener;
import sc.protocol.LobbyClient;
import sc.sample.protocol.ProtocolDefinition;
import sc.sample.server.GamePluginImpl;
import sc.sample.shared.Move;

import com.thoughtworks.xstream.XStream;

public class SimpleClient implements ILobbyClientListener
{
	private LobbyClient	client;

	public SimpleClient(XStream xStream) throws IOException
	{
		client = new LobbyClient(xStream, "localhost", PORT);
		this.client.addListener(this);
	}

	private static final int	PORT	= 13050;

	@Override
	public void onRoomMessage(String roomId, Object data)
	{
		client.sendMessageToRoom(roomId, new Move(1, 1));
	}

	@Override
	public void onError(ErrorResponse response)
	{
		System.err.println(response);
	}

	@Override
	public void onNewState(String roomId, Object state)
	{
		System.out.println("new state received" + state);
	}

	public void joinAnyGame()
	{
		client.joinAnyGame(GamePluginImpl.PLUGIN_UUID);
	}
}
