package sc.server.network;

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
	protected Collection<Client>			clients;
	private NewClientListener				clientListener	= new NewClientListener();
	private List<IClientManagerListener>	listeners		= new LinkedList<IClientManagerListener>();
	private boolean							running			= false;

	public ClientManager()
	{
		clients = new LinkedList<Client>();
		this.clientListener = new NewClientListener();
	}

	public void add(Client newClient)
	{
		clients.add(newClient);
	}

	public void remove(Client client)
	{
		clients.remove(client);
	}

	public void addAll(Collection<Client> newClients)
	{
		for (Client client : newClients)
		{
			add(client);
		}
	}

	@Override
	public void run()
	{
		running = true;

		logger.info("ClientManager running.");
		
		while (running && !Thread.interrupted())
		{
			for (Client client : this.clientListener.fetchNewClients())
			{
				for (IClientManagerListener listener : listeners)
				{
					listener.onClientConnected(client);
				}
			}

			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				logger.warn("Failed to sleep.", e);
			}
		}

	}

	/**
	 * Starts the ClientManager in it's own daemon thread.
	 */
	public void start()
	{
		ServiceManager.createService(this).start();
	}

	public void addListener(IClientManagerListener listener)
	{
		this.listeners.add(listener);
	}

	public void close()
	{
		this.running = false;
	}
}
