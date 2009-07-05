package sc.sample.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import com.thoughtworks.xstream.XStream;

import sc.protocol.LobbyClient;
import sc.protocol.LobbyProtocol;
import sc.sample.protocol.ProtocolDefinition;
import sc.sample.server.GamePluginImpl;
import sc.sample.shared.Move;

public class SimpleClient extends LobbyClient
{
	public SimpleClient(XStream xStream) throws IOException
	{
		super(GamePluginImpl.PLUGIN_UUID, xStream, "localhost", PORT);
	}

	private static final int	PORT	= 3000;

	@Override
	protected void onRoomMessage(String roomId, Object data)
	{
		this.sendMessageToRoom(roomId, new Move(1, 1));
	}

	@Override
	protected Collection<Class<? extends Object>> getProtocolClasses()
	{
		return ProtocolDefinition.getProtocolClasses();
	}
}
