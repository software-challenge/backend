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
		public Object LastPacket = null;
		
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

	private static final String	EMPTY_OBJECT_STREAM	= "<object-stream></object-stream>";
	
	@Before
	public void setup() {
		Configuration.getXStream().alias("example", ExamplePacket.class);
	}
	
	@Test
	public void clientReceivePacketTest() throws IOException
	{
		StringNetworkInterface stringInterface = new StringNetworkInterface("<object-stream><example /></object-stream>");
		StupidClientListener clientListener = new StupidClientListener();
		Client client = new Client(stringInterface, Configuration.getXStream());
		client.addClientListener(clientListener);
		client.receive();
		Assert.assertNotNull(clientListener.LastPacket);
		Assert.assertTrue(clientListener.LastPacket instanceof ExamplePacket);
	}
	
	@Test
	public void clientSendPacketTest() throws IOException
	{
		StringNetworkInterface stringInterface = new StringNetworkInterface(EMPTY_OBJECT_STREAM);
		Client client = new Client(stringInterface, Configuration.getXStream());
		client.send(new ExamplePacket());
		String data = stringInterface.getData();
		Assert.assertTrue(data.startsWith("<object-stream>\n"+
				"  <example"));
	}
}
