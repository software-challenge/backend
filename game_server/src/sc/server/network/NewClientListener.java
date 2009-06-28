package sc.server.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.server.Configuration;
import sc.server.network.interfaces.TcpNetwork;



public class NewClientListener implements Runnable
{
	protected static final Logger	logger			= LoggerFactory
															.getLogger(NewClientListener.class);
	private Object					newClientLock	= new Object();
	private Collection<Client>		newClients		= new HashSet<Client>();
	private ServerSocket			serverSocket;

	public Collection<Client> fetchNewClients()
	{
		Collection<Client> result = null;
		
		synchronized (newClientLock)
		{
			result = new ArrayList<Client>(this.newClients);
		}
		
		return result;
	}

	private void acceptClient()
	{
		try
		{
			Socket clientSocket = this.serverSocket.accept();
			Client newClient = new Client(new TcpNetwork(clientSocket), Configuration.getXStream());

			logger.info("A new client connected...");

			synchronized (newClientLock)
			{
				newClients.add(newClient);
			}
		}
		catch (IOException e)
		{
			logger.warn("Unexpected exception occurred {}", e);
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		start();

		while (!serverSocket.isClosed() && !Thread.interrupted())
		{
			acceptClient();
			Thread.yield();
		}
	}

	public void start()
	{
		startSocketListener();
	}

	private void startSocketListener()
	{
		int port = Configuration.getPort();

		try
		{
			this.serverSocket = new ServerSocket(port);
			logger.info("Listening on port {} for incoming connections.", port);
		}
		catch (IOException e)
		{
			throw new RuntimeException("Could not start server", e);
		}
	}
}
