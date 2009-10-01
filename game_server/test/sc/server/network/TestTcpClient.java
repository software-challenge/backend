package sc.server.network;

import java.io.IOException;
import java.net.Socket;

import sc.networking.TcpNetwork;
import sc.networking.clients.XStreamClient;

import com.thoughtworks.xstream.XStream;

public class TestTcpClient extends XStreamClient
{
	public TestTcpClient(XStream xstream, Socket socket) throws IOException
	{
		super(xstream, new TcpNetwork(socket));
	}

	@Override
	protected void onObject(Object o)
	{
		// ignore it
		// LoggerFactory.getLogger(this.getClass()).debug("Received: {}", o);
	}
}
