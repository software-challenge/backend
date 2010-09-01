package sc.server.network;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.exceptions.RescueableClientException;
import sc.networking.INetworkInterface;
import sc.networking.clients.XStreamClient;
import sc.protocol.responses.ErrorResponse;
import sc.server.Configuration;

import com.thoughtworks.xstream.XStream;

/**
 * A generic client.
 */
public class Client extends XStreamClient implements IClient
{
	private final Set<IClientListener>		clientListeners			= new HashSet<IClientListener>();
	private final Collection<IClientRole>	roles					= new LinkedList<IClientRole>();
	private boolean							notifiedOnDisconnect	= false;
	private static final Logger				logger					= LoggerFactory
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
		if (!isClosed())
		{
			super.send(packet);
		}
		else
		{
			logger.warn("Writing on a closed Stream -> dropped the packet.");
		}
	}

	@Override
	public void close()
	{
		if (!isClosed())
		{
			logger.info("Closing Client {}", this);
			super.close();
			onDisconnect(DisconnectCause.REGULAR);
		}
		else
		{
			logger.warn("Reclosing an already closed stream");
		}
	}

	private void notifyOnPacket(Object packet)
	{
		Set<RescueableClientException> errors = new HashSet<RescueableClientException>();

		PacketCallback callback = new PacketCallback(packet);

		for (IClientListener listener : this.clientListeners)
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

		if (errors.isEmpty() && !callback.isProcessed())
		{
			logger.warn("Packet {} wasn't processed.", packet);
			errors.add(new RescueableClientException(
					"The packet wasn't processed/recognized."));
		}

		for (RescueableClientException error : errors)
		{
			logger.warn("An error occured: ", error);
			
			
			//if(error.getClass().equals(GameLogicException.class) && (error.getMessage()=="Move was invalid" || error.getMessage()=="Unknown ObjectType received.")){
			if(error.getMessage() != "It's not your turn yet.") {
				Object resp = new ErrorResponse(packet, error.getMessage());
				notifyOnError(resp);
				super.close();
				logger.warn("Game closed because of GameLogicException! The message is: " + error.getMessage());
			}
		}
	}
	
	private synchronized void notifyOnError(Object packet)
	{
		for (IClientListener listener : this.clientListeners)
		{
			try
			{
				listener.onError(this, packet);
			}
			catch (Exception e)
			{
				logger
						.error(
								"OnError Notification caused an exception.",
								e);
			}
		}
	}

	private synchronized void notifyOnDisconnect()
	{
		if (!this.notifiedOnDisconnect)
		{
			this.notifiedOnDisconnect = true;

			for (IClientListener listener : this.clientListeners)
			{
				try
				{
					listener.onClientDisconnected(this);
				}
				catch (Exception e)
				{
					logger
							.error(
									"OnDisconnect Notification caused an exception.",
									e);
				}
			}
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
		for (IClientRole role : this.roles)
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
	protected void onDisconnect(DisconnectCause cause)
	{
		for (IClientRole role : this.roles)
		{
			try
			{
				role.close();
			}
			catch (Exception e)
			{
				logger.warn("Couldn't close role.", e);
			}
		}

		notifyOnDisconnect();
	}

	@Override
	protected void onObject(Object o)
	{
		this.notifyOnPacket(o);
	}

	public void sendAsynchronous(Object packet)
	{
		// TODO make it async
		this.send(packet);
	}
}
