package sc.server.network;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.networking.TcpNetwork;
import sc.server.Configuration;
import sc.server.ServiceManager;

public class NewClientListener implements Runnable, Closeable
{
	protected static final Logger	logger			= LoggerFactory
															.getLogger(NewClientListener.class);
	private Object					newClientLock	= new Object();
	private Queue<Client>			newClients		= new LinkedList<Client>();
	private ServerSocket			serverSocket	= null;

	public static int				lastUsedPort	= 0;

	public Collection<Client> fetchNewClients()
	{
		Collection<Client> result = new LinkedList<Client>();

		synchronized (newClientLock)
		{
			for (int i = 0; i < 10; i++)
			{
				Client c = newClients.poll();

				if (c != null)
				{
					result.add(c);
				}
				else
				{
					break;
				}
			}
		}

		return result;
	}

	private void acceptClient()
	{
		try
		{
			Socket clientSocket = this.serverSocket.accept();

			logger.info("A Client connected...");

			Client newClient = new Client(new TcpNetwork(clientSocket),
					Configuration.getXStream());

			synchronized (newClientLock)
			{
				newClients.add(newClient);
				logger.info("Added Client to ReadyQueue.");
			}
		}
		catch (IOException e)
		{
			if (this.serverSocket.isClosed())
			{
				logger.warn("ServerSocket has been closed.");
			}
			else
			{
				logger.error("Unexpected exception occurred {}", e);
			}
		}
	}

	@Override
	public void run()
	{
		while (!serverSocket.isClosed() && !Thread.interrupted())
		{
			acceptClient();
			Thread.yield();
		}
	}

	public void start()
	{
		startSocketListener();
		ServiceManager.createService(this.getClass().getSimpleName(), this)
				.start();
	}

	private void startSocketListener()
	{
		int port = Configuration.getPort();

		try
		{
			this.serverSocket = new ServerSocket(port);
			int usedPort = this.serverSocket.getLocalPort();
			lastUsedPort = usedPort;
			logger.info("Listening on port {} for incoming connections.",
					usedPort);
		}
		catch (IOException e)
		{
			throw new RuntimeException(
					"Could not start server on port " + port, e);
		}
	}

	public void close()
	{
		try
		{
			logger.info("Shutting down NewClientListener...");

			if (this.serverSocket != null)
			{
				this.serverSocket.close();
			}
		}
		catch (IOException e)
		{
			logger.warn("Couldn't close socket.", e);
		}
	}
}
