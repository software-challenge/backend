package sc.protocol;

import java.util.HashSet;
import java.util.Set;

import sc.protocol.requests.JoinPreparedRoomRequest;
import sc.protocol.requests.JoinRoomRequest;

import com.thoughtworks.xstream.XStream;

public abstract class LobbyProtocol
{
	private static Set<XStream>	alreadyProcessed	= new HashSet<XStream>();

	public static void registerMessages(XStream xStream)
	{
		if (!alreadyProcessed.contains(xStream))
		{
			xStream.alias("Join", JoinRoomRequest.class);
			xStream.alias("JoinPrepared", JoinPreparedRoomRequest.class);
			xStream.alias("Error", ErrorResponse.class);
			xStream.alias("Room", RoomPacket.class);

			alreadyProcessed.add(xStream);
		}
	}
}
