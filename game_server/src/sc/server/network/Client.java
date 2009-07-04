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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.RescueableClientException;
import sc.networking.INetworkInterface;
import sc.protocol.ErrorResponse;
import sc.protocol.XStreamClient;
import sc.server.Configuration;

import com.thoughtworks.xstream.XStream;

/**
 * A generic client.
 * 
 * @author mja
 * @author rra
 */
public class Client extends XStreamClient
{
	private boolean					zombie			= false;
	private Set<IClientListener>	clientListeners	= new HashSet<IClientListener>();
	private Collection<IClientRole>	roles			= new LinkedList<IClientRole>();
	private static Logger			logger			= LoggerFactory
															.getLogger(Client.class);

	public Client(INetworkInterface networkInterface, XStream configuredXStream)
			throws IOException
	{
		super(configuredXStream, networkInterface);
	}

	public Collection<IClientRole> getRoles()
	{
		return Collections.unmodifiableCollection(this.roles);
	}

	public void addRole(IClientRole role)
	{
		this.roles.add(role);
	}

	@Override
	public synchronized void send(Object packet)
	{
		super.send(packet);
	}

	public boolean isZombie()
	{
		return zombie;
	}

	@Override
	public void close() throws IOException
	{
		super.close();
		if (!this.zombie)
		{
			this.zombie = true;
			notifyOnDisconnect();
		}
	}

	private void notifyOnPacket(Object packet)
	{
		Set<RescueableClientException> errors = new HashSet<RescueableClientException>();

		PacketCallback callback = new PacketCallback(packet);

		for (IClientListener listener : clientListeners)
		{
			try
			{
				listener.onRequest(this, callback);
			}
			catch (RescueableClientException e)
			{
				errors.add(e);
			}
		}

		if (!callback.isProcessed())
		{
			logger.warn("Packet {} wasn't processed.", packet);
			errors.add(new RescueableClientException(
					"The packet wasn't processed/recognized."));
		}

		for (RescueableClientException error : errors)
		{
			logger.warn("An error occured: ", error);
			this.send(new ErrorResponse(packet, error.getMessage()));
		}
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

	/**
	 * 
	 * @return true, if this client has an AdministratorRole
	 */
	public boolean isAdministrator()
	{
		for (IClientRole role : roles)
		{
			if (role instanceof AdministratorRole)
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Authenticates a Client as Administrator
	 * 
	 * @param password
	 *            The secret which is required to gain administrative rights.
	 * @throws AuthenticationFailedException
	 */
	public void authenticate(String password)
			throws AuthenticationFailedException
	{
		String correctPassword = Configuration.getAdministrativePassword();

		if (correctPassword != null && correctPassword.equals(password))
		{
			if (!isAdministrator())
			{
				this.addRole(new AdministratorRole(this));
				logger.info("Client authenticated as administrator");
			}
			else
			{
				logger
						.warn("Client tried to authenticate as administrator twice.");
			}
		}
		else
		{
			logger.warn("Client failed to authenticate as administrator.");

			throw new AuthenticationFailedException();
		}
	}

	@Override
	public String toString()
	{
		return String.format("Client(interface=%s)", networkInterface);
	}

	@Override
	public void onDisconnect(DisconnectCause cause)
	{
		super.onDisconnect(cause);

		logger.info("{} disconnected.", this);

		notifyOnDisconnect();
	}

	@Override
	protected void onObject(Object o)
	{
		this.notifyOnPacket(o);
	}
}
