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
	private class StupidClientListener implements IClientListener
	{
		public Object	LastPacket	= null;

		@Override
		public void onRequest(Client source, Object packet)
		{
			LastPacket = packet;
		}

		@Override
		public void onClientDisconnected(Client source)
		{
			// I don't care
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

	@Test
	public void clientReceivePacketTest() throws IOException
	{
		StringNetworkInterface stringInterface = new StringNetworkInterface(
				"<protocol><example /></protocol>");
		StupidClientListener clientListener = new StupidClientListener();
		MockClient client = new MockClient(stringInterface, Configuration.getXStream());
		client.addClientListener(clientListener);
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
		client.send(new ExamplePacket());
		String data = stringInterface.getData();
		Assert.assertTrue(data.startsWith("<protocol>\n  <example"));
	}
}
