package sc.server;

import sc.server.protocol.InboundPacket;
import sc.server.protocol.OutboundPacket;

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
		xStream.alias("clientpacket", InboundPacket.class);
		xStream.alias("serverpacket", OutboundPacket.class);
	}
	
	public static int getPort()
	{
		return 3000;
	}

	public static XStream getXStream()
	{
		// TODO: configure
		return xStream;
	}
}
