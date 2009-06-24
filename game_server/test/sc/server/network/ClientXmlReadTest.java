package sc.server.network;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import sc.server.Configuration;
import sc.server.helpers.StringNetworkInterface;
import sc.server.network.Client;
import sc.server.network.IClientListener;
import sc.server.protocol.InboundPacket;




public class ClientXmlReadTest
{
	private class StupidClientListener implements IClientListener
	{
		public InboundPacket LastPacket = null;
		
		@Override
		public void onPacketReceived(Client source, InboundPacket packet)
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
	
	@Test
	public void clientReceivePacketTest() throws IOException
	{
		StringNetworkInterface stringInterface = new StringNetworkInterface("<object-stream><clientpacket /></object-stream>");
		StupidClientListener clientListener = new StupidClientListener();
		Client client = new Client(stringInterface, Configuration.getXStream());
		client.addClientListener(clientListener);
		client.receive();
		Assert.assertNotNull(clientListener.LastPacket);
	}
	
	@Test
	public void clientSendPacketTest() throws IOException
	{
		StringNetworkInterface stringInterface = new StringNetworkInterface(EMPTY_OBJECT_STREAM);
		Client client = new Client(stringInterface, Configuration.getXStream());
		client.send(null);
		String data = stringInterface.getData();
		Assert.assertTrue(data.startsWith("<object-stream>\n"+
				"  <serverpacket"));
	}
}
