package sc.server.network;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import com.thoughtworks.xstream.XStream;

import sc.protocol.RoomPacket;
import sc.server.Configuration;
import sc.server.helpers.StringNetworkInterface;

public class MockClient extends Client
{
	private Queue<Object>	outgoingMessages	= new LinkedList<Object>();
	private Object			object				= null;

	public MockClient(StringNetworkInterface stringInterface, XStream xStream)
			throws IOException
	{
		super(stringInterface, xStream);
	}

	public MockClient() throws IOException
	{
		super(new StringNetworkInterface("<protocol>"), Configuration
				.getXStream());
	}

	@Override
	public synchronized void send(Object packet)
	{
		outgoingMessages.add(packet);
		super.send(packet);
	}

	public Object popMessage()
	{
		return outgoingMessages.poll();
	}

	@SuppressWarnings("unchecked")
	public <T> T seekMessage(Class<T> type)
	{
		Object current = null;
		do
		{
			current = popMessage();
		} while (current != null && current.getClass() != type);

		if (current == null)
		{
			throw new RuntimeException(
					"Could not find a message of the specified type");
		}
		else
		{
			return (T) current;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T seekRoomMessage(String roomId, Class<T> type)
	{
		Object current = null;
		do
		{
			RoomPacket response = seekMessage(RoomPacket.class);
			if (roomId.equals(response.getRoomId()))
			{
				current = response.getData();
			}
		} while (current != null && current.getClass() != type);

		if (current == null)
		{
			throw new RuntimeException(
					"Could not find a message of the specified type");
		}
		else
		{
			return (T) current;
		}
	}

	@Override
	protected void onObject(Object o)
	{
		super.onObject(o);
		this.object = o;
	}

	public Object receive()
	{
		while (object == null)
		{
			Thread.yield();
		}

		return object;
	}
}
