package sc.server.network;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

import sc.api.plugins.exceptions.RescuableClientException;
import sc.networking.INetworkInterface;
import sc.networking.clients.XStreamClient;
import sc.protocol.responses.ErrorResponse;
import sc.protocol.responses.LeftGameEvent;
import sc.server.Configuration;

/**
 * A generic client. This represents a client in the server. Clients which
 * connect to the server (as separate programs or running as threads started by
 * the server) are represented by {@link sc.networking.clients.LobbyClient}.
 */
public class Client extends XStreamClient implements IClient
{
	private final LinkedList<IClientListener>	clientListeners			= new LinkedList<IClientListener>();
	private final Collection<IClientRole>		roles					= new LinkedList<IClientRole>();
	private boolean								notifiedOnDisconnect	= false;
	private static final Logger					logger					= LoggerFactory
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

	@Override
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
			logger.warn(
					"Writing on a closed Stream -> dropped the packet. (tried to send package of type {}) Thread: {}",
					packet.getClass().getSimpleName(),
					Thread.currentThread().getName());
		}
		// FIXME this solves the problem of Clients not terminated when the
		// other client makes an invalid move, but this is not the right way to
		// do it!
		/*if (packet instanceof LeftGameEvent)
		{
			logger.debug("Stopping {} because of sending of LeftGameEvent",
					Thread.currentThread().getName());
			stop();
		}*/
	}

	private void notifyOnPacket(Object packet)
	{
		/*
		 * NOTE that method is called in the receiver thread. Messages should
		 * only be passed to listeners. No callbacks should be invoked directly
		 * in the receiver thread.
		 */

		Set<RescuableClientException> errors = new HashSet<RescuableClientException>();

		PacketCallback callback = new PacketCallback(packet);

		for (IClientListener listener : this.clientListeners)
		{
			try
			{
				listener.onRequest(this, callback);
			}
			catch (RescuableClientException e)
			{
				errors.add(e);
			}
		}

		if (errors.isEmpty() && !callback.isProcessed())
		{
			logger.warn("Packet {} wasn't processed.", packet);
			errors.add(new RescuableClientException(
					"The packet wasn't processed/recognized."));
		}

		for (RescuableClientException error : errors)
		{
			logger.warn("An error occured: ", error);

			if (error.getMessage() != "It's not your turn yet.")
			{
				Object resp = new ErrorResponse(packet, error.getMessage());
				notifyOnError(resp);
				logger.warn(
						"Game closed because of GameLogicException! The message is: "
								+ error.getMessage());
			}
		}
		if (!errors.isEmpty())
		{
			logger.debug("FOCUS stopping client because of error. Thread: {}",
					Thread.currentThread().getName());
			stop();
		}
		if (packet instanceof LeftGameEvent)
		{
			logger.debug(
					"FOCUS stopping client because of LeftGameEvent received. Thread: {}",
					Thread.currentThread().getName());
			stop();
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
				logger.error("OnError Notification caused an exception.", e);
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
					logger.error(
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
				addRole(new AdministratorRole(this));
				logger.info("Client authenticated as administrator");
			}
			else
			{
				logger.warn(
						"Client tried to authenticate as administrator twice.");
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
		super.onDisconnect(cause);
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
		/*
		 * NOTE that this method is called in the receiver thread. Messages
		 * should only be passed to listeners. No callbacks should be invoked
		 * directly in the receiver thread.
		 */
		notifyOnPacket(o);
	}

	@Override
	public void sendAsynchronous(Object packet)
	{
		// TODO make it async
		send(packet);
	}
}
