package sc.protocol;

import java.util.Collection;

import sc.framework.plugins.RoundBasedGameInstance;
import sc.framework.plugins.SimpleGameInstance;
import sc.framework.plugins.SimplePlayer;
import sc.protocol.requests.AuthenticateRequest;
import sc.protocol.requests.JoinPreparedRoomRequest;
import sc.protocol.requests.JoinRoomRequest;
import sc.protocol.requests.PrepareGameRequest;

import com.thoughtworks.xstream.XStream;

public abstract class LobbyProtocol
{
	public static XStream registerMessages(XStream xStream)
	{
		return registerMessages(xStream, null);
	}

	public static XStream registerMessages(XStream xStream,
			Collection<Class<?>> additionClasses)
	{
		xStream.processAnnotations(ErrorResponse.class);
		xStream.processAnnotations(RoomPacket.class);
		xStream.processAnnotations(AuthenticateRequest.class);
		xStream.processAnnotations(JoinPreparedRoomRequest.class);
		xStream.processAnnotations(JoinRoomRequest.class);
		xStream.processAnnotations(PrepareGameRequest.class);
		xStream.processAnnotations(MementoPacket.class);
		xStream.processAnnotations(SimplePlayer.class);
		xStream.processAnnotations(SimpleGameInstance.class);
		xStream.processAnnotations(RoundBasedGameInstance.class);

		if (additionClasses != null)
		{
			for (Class<?> cls : additionClasses)
			{
				xStream.processAnnotations(cls);
			}
		}

		return xStream;
	}
}
