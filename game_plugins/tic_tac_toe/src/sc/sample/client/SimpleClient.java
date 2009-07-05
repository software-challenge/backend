package sc.sample.client;

import java.io.IOException;

import com.thoughtworks.xstream.XStream;

import sc.protocol.LobbyClient;
import sc.sample.server.GamePluginImpl;
import sc.sample.shared.Move;

public class SimpleClient extends LobbyClient
{
	public SimpleClient() throws IOException
	{
		super(GamePluginImpl.PLUGIN_UUID, new XStream(), "localhost", PORT);
	}

	private static final int	PORT	= 3000;

	@Override
	protected void onRoomMessage(String roomId, Object data)
	{
		this.sendMessageToRoom(roomId, new Move(1, 1));
	}
}
