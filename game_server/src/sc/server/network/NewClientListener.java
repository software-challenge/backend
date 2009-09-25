package sc.server.network;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.networking.TcpNetwork;
import sc.server.Configuration;
import sc.server.ServiceManager;

public class NewClientListener implements Runnable, Closeable
{
	protected static final Logger		logger			= LoggerFactory
																.getLogger(NewClientListener.class);
	/**
	 * @deprecated by chw
	 */
	private final Object				newClientLock	= new Object();
	/**
	 * @deprecated by chw
	 */
	private final Queue<Client>			newClients		= new LinkedList<Client>();

	private final BlockingQueue<Client>	queue			= new LinkedBlockingQueue<Client>();
	private ServerSocket				serverSocket	= null;

	public static int					lastUsedPort	= 0;

	/**
	 * @deprecated by chw
	 * @return
	 */
	public Collection<Client> fetchNewClients()
	{
		Collection<Client> result = new LinkedList<Client>();

		synchronized (this.newClientLock)
		{
			for (int i = 0; i < 10; i++)
			{
				Client c = this.newClients.poll();

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

	/**
	 * Returns a new connected client, if a new one is available. Otherwise this
	 * method blocks until a new client connects.
	 * 
	 * @return
	 * @throws InterruptedException
	 *             If interrupted while waiting for a new client.
	 */
	public Client fetchNewSingleClient() throws InterruptedException
	{
		return this.queue.take();
	}

	private void acceptClient()
	{
		try
		{
			Socket clientSocket = this.serverSocket.accept();

			logger.info("A Client connected...");

			Client newClient = new Client(new TcpNetwork(clientSocket),
					Configuration.getXStream());

			/*
			 * synchronized (this.newClientLock) {
			 * this.newClients.add(newClient);
			 * logger.info("Added Client to ReadyQueue."); }
			 */

			try
			{
				this.queue.put(newClient);
				logger.info("Added Client to ReadyQueue.");
			}
			catch (InterruptedException e)
			{
				logger.error("Client could not be added to ready queue.", e);
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
		while (!this.serverSocket.isClosed() && !Thread.interrupted())
		{
			acceptClient();
			Thread.yield();
		}
	}

	public void start() throws IOException
	{
		startSocketListener();
		ServiceManager.createService(this.getClass().getSimpleName(), this)
				.start();
	}

	private void startSocketListener() throws IOException
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
			throw new IOException("Could not start server on port " + port, e);
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
