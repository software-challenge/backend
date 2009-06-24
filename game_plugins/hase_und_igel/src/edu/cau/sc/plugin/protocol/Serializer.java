package edu.cau.sc.plugin.protocol;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

import com.thoughtworks.xstream.XStream;

import edu.cau.sc.plugin.Board;
import edu.cau.sc.plugin.Field;
import edu.cau.sc.plugin.Player;

/**
 * Provides <code>XStream</code> based serialization for <code>AbstractPacket</code>
 * @author raphael
 * @since 15.04.2009
 */
public class Serializer
{
	private static XStream instance;
	
	private static XStream getInstance()
	{
		if (instance == null)
		{
			// Setup XStream
			instance = new XStream();

			instance.alias("board", Board.class);
			instance.alias("field", Field.class);
			instance.alias("player", Player.class);
			
			// TODO setup Packet aliases
		}
		return instance;
	}
	
	public static ByteBuffer serialize(AbstractPacket obj)
	{
		XStream xstream = getInstance();
		return ByteBuffer.wrap(xstream.toXML(obj).getBytes());
	}
	
	public static AbstractPacket deserialize(ByteBuffer data)
	{
		XStream xstream = getInstance();
		return (AbstractPacket)xstream.fromXML(new ByteArrayInputStream(data.array()));
	}
}
