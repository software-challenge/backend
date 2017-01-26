package sc.server.network;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.exceptions.RescueableClientException;
import sc.server.IClientManagerListener;
import sc.server.ServiceManager;

/**
 * The ClientManager serves as a lookup table for all active connections.
 */
public class ClientManager implements Runnable, IClientListener
{
	private static final Logger					logger		= LoggerFactory
																	.getLogger(ClientManager.class);
	protected final LinkedList<Client>			clients		= new LinkedList<Client>();
	private final NewClientListener				clientListener;
	private final List<IClientManagerListener>	listeners	= new LinkedList<IClientManagerListener>();
	private boolean								running		= false;
	private Thread								thread 		= null;

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

		newClient.addClientListener(this);

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
				if (this.running) {
					logger.error("Interrupted while waiting for a new client.", e);
				} else {
					logger.error("Client manager is shutting down");
				}
				// TODO should it be handled?
			}

		}

		this.running = false;
		logger.info("ClientManager closed.");
	}

	/**
	 * Starts the ClientManager in it's own daemon thread.
	 */
	public void start() throws IOException
	{
		this.clientListener.start();
		if (this.thread == null) {
			this.thread = ServiceManager.createService(this.getClass().getSimpleName(), this);
			this.thread.start();
		}
	}

	public void addListener(IClientManagerListener listener)
	{
		this.listeners.add(listener);
	}

	public void close()
	{
		this.running = false;

		if(this.thread != null) {
			this.thread.interrupt();
		}

		this.clientListener.close();

		for (int i = 0; i < this.clients.size(); i++)
		{
			Client client = this.clients.get(i);
			client.stop();
		}
	}

	@Override
	public void onClientDisconnected(Client source)
	{
		final Client client = source;
		new Thread(new Runnable() {
			@Override
			public void run()
			{
				synchronized(client) {
					logger.info("Removing client {} from client manager", client);
					ClientManager.this.clients.remove(client);
				}
			}
		}).start();
		//this.clients.remove(source);
		/*if (this.running) {
			logger.info("Removing client {} from client manager", source);
			final Client client = source;
			new Thread(new Runnable() {
				@Override
				public void run()
				{
					synchronized(client) {
						ClientManager.this.clients.remove(client);
					}
				}
			}).start();
		}*/
	}

	@Override
	public void onError(Client source, Object packet)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequest(Client source, PacketCallback packet)
			throws RescueableClientException
	{
		// TODO Auto-generated method stub

	}
}
