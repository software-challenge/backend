package sc.protocol;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import sc.protocol.requests.AuthenticateRequest;
import sc.protocol.requests.JoinPreparedRoomRequest;
import sc.protocol.requests.JoinRoomRequest;
import sc.protocol.requests.PrepareGameRequest;

import com.thoughtworks.xstream.XStream;

public abstract class LobbyProtocol
{
	public static void registerMessages(XStream xStream)
	{
		registerMessages(xStream, null);
	}

	public static void registerMessages(XStream xStream,
			Collection<Class<?>> additionClasses)
	{
		xStream.processAnnotations(ErrorResponse.class);
		xStream.processAnnotations(RoomPacket.class);
		xStream.processAnnotations(AuthenticateRequest.class);
		xStream.processAnnotations(JoinPreparedRoomRequest.class);
		xStream.processAnnotations(JoinRoomRequest.class);
		xStream.processAnnotations(PrepareGameRequest.class);

		if (additionClasses != null)
		{
			for (Class<?> cls : additionClasses)
			{
				xStream.processAnnotations(cls);
			}
		}
	}
}
