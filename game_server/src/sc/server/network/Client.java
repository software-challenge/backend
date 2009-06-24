package sc.server.network;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import sc.api.plugins.protocol.IPluginPacket;
import sc.server.network.interfaces.INetworkInterface;
import sc.server.protocol.InboundPacket;
import sc.server.protocol.OutboundPacket;

import com.thoughtworks.xstream.XStream;


/**
 * A generic client.
 * 
 * @author mja
 * @author rra
 */
public class Client implements Runnable
{
	private final INetworkInterface						networkInterface;
	private final ObjectOutputStream			outputStream;
	private final ObjectInputStream				inputStream;
	private boolean								zombie			= false;
	private Set<IClientListener>				clientListeners	= new HashSet<IClientListener>();
	private Map<Class<?>, IClientRole>	roles			= new HashMap<Class<?>, IClientRole>();

	public Client(INetworkInterface networkInterface, XStream configuredXStream) throws IOException
	{
		if(networkInterface == null)
		{
			throw new IllegalArgumentException("networkInterface must not be null.");
		}
		
		if(configuredXStream == null)
		{
			throw new IllegalArgumentException("configuredXStream must not be null.");
		}
		
		this.networkInterface = networkInterface;
		this.outputStream = configuredXStream
				.createObjectOutputStream(new OutputStreamWriter(networkInterface
						.getOutputStream()));
		this.inputStream = configuredXStream
				.createObjectInputStream(new InputStreamReader(networkInterface
						.getInputStream()));
	}

	public IClientRole getRole(Class<?> role)
	{
		return this.roles.get(role);
	}

	public Collection<IClientRole> getRoles(Socket socket)
	{
		return this.roles.values();
	}

	public synchronized void send(IPluginPacket message)
	{
        OutboundPacket packet = new OutboundPacket(message);

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
            catch(Exception e)
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

		InboundPacket packet = null;

		try
		{
			packet = InboundPacket.readFromStream(inputStream);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

        if(packet != null)
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
    
    private void notifyOnPacket(InboundPacket packet)
    {
        for (IClientListener listener : clientListeners)
		{
			listener.onPacketReceived(this, packet);
		}
    }

    private void notifyOnDisconnect()
    {
        for(IClientListener listener : clientListeners)
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
}
