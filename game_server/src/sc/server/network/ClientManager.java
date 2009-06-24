package sc.server.network;

import java.util.Collection;
import java.util.LinkedList;

/**
 * The ClientManager serves as a lookup table for all active connections.
 */
public class ClientManager implements Runnable
{
	protected Collection<Client>	clients;
	private NewClientListener		clientListener;

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
		// reap the zombie clients
		for (Client client : this.clients)
		{
			// InboundPacket msg = client.fetchMessage();
		}
	}

	public void start()
	{
		Thread thread = new Thread();
		thread.start();
	}
}
