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
			this.addAll(this.clientListener.fetchNewClients());

			try
			{
				Thread.sleep(10);
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
	public void start()
	{
		ServiceManager.createService(this.getClass().getSimpleName(), this).start();
	}

	public void addListener(IClientManagerListener listener)
	{
		this.listeners.add(listener);
	}

	public void close()
	{
		for(Client client : clients)
		{
			client.close();
		}
		
		this.running = false;
	}
}
