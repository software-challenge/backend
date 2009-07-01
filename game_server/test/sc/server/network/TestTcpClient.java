package sc.server.network;

import java.io.IOException;
import java.net.Socket;

import org.junit.Assert;

import sc.networking.TcpNetwork;
import sc.protocol.XStreamClient;
import sc.server.Configuration;

import com.thoughtworks.xstream.XStream;

public class TestTcpClient extends XStreamClient
{
	public TestTcpClient(XStream xstream, Socket socket) throws IOException
	{
		super(xstream, new TcpNetwork(socket));
	}

	public static TestTcpClient connect()
	{
		try
		{
			if (NewClientListener.lastUsedPort == 0)
			{
				throw new RuntimeException(
						"Could not find an open port to connect to.");
			}

			Socket mySocket = new Socket("localhost",
					NewClientListener.lastUsedPort);
			return new TestTcpClient(Configuration.getXStream(), mySocket);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Assert.fail("Could not connect to server.");
			return null;
		}
	}

	@Override
	public void onDisconnect(DisconnectCause cause)
	{
		super.onDisconnect(cause);
	}

	@Override
	protected void onObject(Object o)
	{
		// ignore
	}
}
