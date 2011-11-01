package sc.server.network;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sc.server.Configuration;
import sc.server.helpers.ExamplePacket;
import sc.server.helpers.StringNetworkInterface;
import sc.server.network.Client;
import sc.server.network.IClientListener;

public class ClientXmlReadTest
{
	private static class StupidClientListener implements IClientListener
	{
		public Object	LastPacket	= null;

		@Override
		public void onRequest(Client source, PacketCallback callback)
		{
			callback.setProcessed();
			this.LastPacket = callback.getPacket();
		}

		@Override
		public void onClientDisconnected(Client source)
		{
			// I don't care
		}

		@Override
		public void onError(Client source, Object packet)
		{
			// TODO Auto-generated method stub
			
		}
	}

	/**
	 * Denotes an empty ObjectStream (to be used with XStream).
	 */
	private static final String	EMPTY_OBJECT_STREAM	= "<protocol></protocol>";

	@Before
	public void setup()
	{
		Configuration.getXStream().alias("example", ExamplePacket.class);
	}

	@Test//(timeout=2000)
	public void clientReceivePacketTest() throws IOException, InterruptedException
	{
		StringNetworkInterface stringInterface = new StringNetworkInterface(
				"<protocol>\n<example />");
		StupidClientListener clientListener = new StupidClientListener();
		MockClient client = new MockClient(stringInterface, Configuration.getXStream());
		client.addClientListener(clientListener);
		client.start();
		Assert.assertNotNull(client.receive());
		Assert.assertNotNull(clientListener.LastPacket);
		Assert.assertTrue(clientListener.LastPacket instanceof ExamplePacket);
	}

	@Test
	public void clientSendPacketTest() throws IOException
	{
		StringNetworkInterface stringInterface = new StringNetworkInterface(
				EMPTY_OBJECT_STREAM);
		Client client = new Client(stringInterface, Configuration.getXStream());
		client.start();
		client.send(new ExamplePacket());
		String data = stringInterface.getData();
		Assert.assertTrue(data.startsWith("<protocol>\n  <example"));
	}
}
