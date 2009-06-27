package sc.server.network;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import sc.server.RescueableClientException;
import sc.server.network.interfaces.INetworkInterface;
import com.thoughtworks.xstream.XStream;

/**
 * A generic client.
 * 
 * @author mja
 * @author rra
 */
public class Client implements Runnable
{
	private final INetworkInterface		networkInterface;
	private final ObjectOutputStream	outputStream;
	private final ObjectInputStream		inputStream;
	private boolean						zombie			= false;
	private Set<IClientListener>		clientListeners	= new HashSet<IClientListener>();
	private Collection<IClientRole>		roles			= new LinkedList<IClientRole>();

	public Client(INetworkInterface networkInterface, XStream configuredXStream)
			throws IOException
	{
		if (networkInterface == null)
		{
			throw new IllegalArgumentException(
					"networkInterface must not be null.");
		}

		if (configuredXStream == null)
		{
			throw new IllegalArgumentException(
					"configuredXStream must not be null.");
		}

		this.networkInterface = networkInterface;
		this.outputStream = configuredXStream
				.createObjectOutputStream(new OutputStreamWriter(
						networkInterface.getOutputStream()));
		this.inputStream = configuredXStream
				.createObjectInputStream(new InputStreamReader(networkInterface
						.getInputStream()));
	}

	public Collection<IClientRole> getRoles()
	{
		return Collections.unmodifiableCollection(this.roles);
	}

	public void addRole(IClientRole role)
	{
		this.roles.add(role);
	}

	public synchronized void send(Object packet)
	{
		try
		{
			outputStream.writeObject(packet);
			outputStream.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			this.zombie = true;
		}
	}

	public boolean isZombie()
	{
		return zombie;
	}

	@Override
	public void run()
	{
		while (!zombie)
		{
			try
			{
				this.receive();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.close();
			}
		}
	}

	void receive()
	{
		if (this.zombie)
		{
			throw new IllegalStateException(
					"Zombie-Clients can't receive any data.");
		}

		Object packet = null;

		try
		{
			packet = readFromStream();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		if (packet != null)
		{
			notifyOnPacket(packet);
		}
		else
		{
			close();
		}
	}

	public void close()
	{
		this.zombie = true;
		notifyOnDisconnect();
	}

	private void notifyOnPacket(Object packet)
	{
		Set<RescueableClientException> errors = new HashSet<RescueableClientException>();

		for (IClientListener listener : clientListeners)
		{
			try
			{
				listener.onRequest(this, packet);
			}
			catch (RescueableClientException e)
			{
				errors.add(e);
			}
		}

		// TODO: send errors back to client
	}

	private void notifyOnDisconnect()
	{
		for (IClientListener listener : clientListeners)
		{
			listener.onClientDisconnected(this);
		}
	}

	public void addClientListener(IClientListener listener)
	{
		this.clientListeners.add(listener);
	}

	public void removeClientListener(IClientListener listener)
	{
		this.clientListeners.remove(listener);
	}

	public Object readFromStream() throws IOException
	{
		try
		{
			return inputStream.readObject();
		}
		catch (Exception e)
		{
			// make sure only ONE type of exception is thrown
			throw new IOException("Could not read data from socket.", e);
		}
	}
}
