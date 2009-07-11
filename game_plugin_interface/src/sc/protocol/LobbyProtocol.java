package sc.protocol;

import java.util.Collection;

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
