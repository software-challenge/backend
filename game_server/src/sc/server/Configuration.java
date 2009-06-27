package sc.server;

import sc.server.protocol.JoinPreparedRoomRequest;
import sc.server.protocol.JoinRoomRequest;

import com.thoughtworks.xstream.XStream;


/**
 * Server configuration.
 * 
 * @author mja
 * @author rra
 * 
 * TODO load values at startup from a properties file
 */
public class Configuration
{
	private static XStream xStream;
	
	static
	{
		xStream = new XStream();
		xStream.alias("Join", JoinRoomRequest.class);
		xStream.alias("JoinPrepared", JoinPreparedRoomRequest.class);
	}
	
	public static int getPort()
	{
		return 3000;
	}

	public static XStream getXStream()
	{
		return xStream;
	}
}
