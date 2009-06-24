package sc.server.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class InboundPacket extends AbstractPacket
{
	public InboundPacket()
	{
		
	}
	
	public static InboundPacket readFromStream(ObjectInputStream inputStream) throws IOException
	{
		try
		{
			return (InboundPacket) inputStream.readObject();
		}
		catch (Exception e)
		{
			// make sure only ONE type of exception is thrown
			throw new IOException("Could not read data from socket.", e);
		}
	}
}
