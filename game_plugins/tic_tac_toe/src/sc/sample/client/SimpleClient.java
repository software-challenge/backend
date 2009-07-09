package sc.sample.client;

import java.io.IOException;
import java.util.Collection;

import sc.protocol.LobbyClient;
import sc.sample.protocol.ProtocolDefinition;
import sc.sample.server.GamePluginImpl;
import sc.sample.shared.Move;

import com.thoughtworks.xstream.XStream;

public class SimpleClient extends LobbyClient
{
	public SimpleClient(XStream xStream) throws IOException
	{
		super(GamePluginImpl.PLUGIN_UUID, xStream, "localhost", PORT);
	}

	private static final int	PORT	= 13050;

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
