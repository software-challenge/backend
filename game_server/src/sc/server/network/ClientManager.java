package sc.server.network;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.server.IClientManagerListener;
import sc.server.ServiceManager;

/**
 * The ClientManager serves as a lookup table for all active connections.
 */
public class ClientManager implements Runnable
{
	private static Logger					logger			= LoggerFactory
																	.getLogger(ClientManager.class);
	protected Collection<Client>			clients			= new LinkedList<Client>();
	private NewClientListener				clientListener	= new NewClientListener();
	private List<IClientManagerListener>	listeners		= new LinkedList<IClientManagerListener>();
	private boolean							running			= false;

	public ClientManager()
	{
		this.clientListener = new NewClientListener();
	}

	public void add(Client newClient)
	{
		clients.add(newClient);

		for (IClientManagerListener listener : listeners)
		{
			listener.onClientConnected(newClient);
		}
	}

	public void addAll(Collection<Client> newClients)
	{
		for (Client client : newClients)
		{
			this.add(client);
		}
	}

	@Override
	public void run()
	{
		running = true;

		logger.info("ClientManager running.");

		while (running && !Thread.interrupted())
		{
			Collection<Client> clients = this.clientListener.fetchNewClients();

			if (!clients.isEmpty())
			{
				logger.info("Delegating new clients to ClientManager.");
				this.addAll(clients);
			}

			try
			{
				Thread.sleep(1);
			}
			catch (InterruptedException e)
			{
				// Interrupts are not critical at this point
			}
		}

	}

	/**
	 * Starts the ClientManager in it's own daemon thread.
	 */
	public void start() throws IOException
	{
		clientListener.start();
		ServiceManager.createService(this.getClass().getSimpleName(), this)
				.start();
	}

	public void addListener(IClientManagerListener listener)
	{
		this.listeners.add(listener);
	}

	public void close()
	{
		this.running = false;

		clientListener.close();

		for (Client client : clients)
		{
			try
			{
				client.close();
			}
			catch (IOException e)
			{
				logger.error("Couldn't close client.", e);
			}
		}
	}
}
