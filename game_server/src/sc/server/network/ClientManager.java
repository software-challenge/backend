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
	private static final Logger					logger		= LoggerFactory
																	.getLogger(ClientManager.class);
	protected final Collection<Client>			clients		= new LinkedList<Client>();
	private final NewClientListener				clientListener;
	private final List<IClientManagerListener>	listeners	= new LinkedList<IClientManagerListener>();
	private boolean								running		= false;

	public ClientManager()
	{
		this.clientListener = new NewClientListener();
	}

	/**
	 * Adds the given <code>newClient</code> and notifies all listeners by
	 * invoking <code>onClientConnected</code>.<br>
	 * <i>(only used by tests and addAll())</i>
	 * 
	 * @param newClient
	 */
	public void add(Client newClient)
	{
		this.clients.add(newClient);

		for (IClientManagerListener listener : this.listeners)
		{
			listener.onClientConnected(newClient);
		}
	}

	@Override
	public void run()
	{
		this.running = true;

		logger.info("ClientManager running.");

		while (this.running && !Thread.interrupted())
		{
			try
			{
				Client client = this.clientListener.fetchNewSingleClient();

				logger.info("Delegating new client to ClientManager...");
				this.add(client);
				logger.info("Delegation done.");
			}
			catch (InterruptedException e)
			{
				logger.error("Interrupted while waiting for a new client.", e);
				// TODO should it be handled?
			}

		}

		logger.info("ClientManager closed.");
	}

	/**
	 * Starts the ClientManager in it's own daemon thread.
	 */
	public void start() throws IOException
	{
		this.clientListener.start();
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

		this.clientListener.close();

		for (Client client : this.clients)
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
