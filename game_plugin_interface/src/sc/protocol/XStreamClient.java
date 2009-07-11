package sc.protocol;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import sc.helpers.IAsyncResult;
import sc.helpers.IRequestResult;
import sc.networking.INetworkInterface;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

public abstract class XStreamClient
{
	private static Logger				logger			= LoggerFactory
																.getLogger(XStreamClient.class);
	protected final INetworkInterface	networkInterface;
	private final ObjectOutputStream	out;
	ObjectInputStream					in;
	private final Thread				thread;
	private DisconnectCause				disconnectCause	= null;
	private boolean						closing			= false;
	protected final XStream				xStream;

	public enum DisconnectCause
	{
		REGULAR, PROTOCOL_ERROR, LOST_CONNECTION, TIMEOUT, UNKNOWN,

		/**
		 * Connection was closed from this side.
		 */
		DISCONNECTED
	}

	public XStreamClient(final XStream xstream,
			final INetworkInterface networkInterface) throws IOException
	{
		if (networkInterface == null)
		{
			throw new IllegalArgumentException(
					"networkInterface must not be null.");
		}

		if (xstream == null)
		{
			throw new IllegalArgumentException("xstream must not be null.");
		}

		this.xStream = xstream;

		this.networkInterface = networkInterface;
		this.out = xstream.createObjectOutputStream(networkInterface
				.getOutputStream(), "protocol");
		this.thread = new Thread(new Runnable() {
			private Logger	threadLogger	= LoggerFactory
													.getLogger(XStreamClient.class);

			@Override
			public void run()
			{

				try
				{
					XStreamClient.this.in = xstream
							.createObjectInputStream(networkInterface
									.getInputStream());

					while (!Thread.interrupted())
					{
						onObject(XStreamClient.this.in.readObject());
					}

					onDisconnect(DisconnectCause.DISCONNECTED);
				}
				catch (EOFException e)
				{
					onDisconnect(DisconnectCause.REGULAR);
				}
				catch (IOException e)
				{
					onDisconnect(DisconnectCause.LOST_CONNECTION);
				}
				catch (ClassNotFoundException e)
				{
					onDisconnect(DisconnectCause.PROTOCOL_ERROR);
					this.threadLogger
							.error(
									"Client violated against the protocol (ClassNotFound).",
									e);
				}
				catch (XStreamException e)
				{
					onDisconnect(DisconnectCause.PROTOCOL_ERROR);
					this.threadLogger.error(
							"Client violated against the protocol.", e);
				}
				catch (Exception e)
				{
					this.threadLogger.error(
							"An error occured while trying to read an object.",
							e);
					onDisconnect(DisconnectCause.UNKNOWN);
				}

				return;
			}
		});
		this.thread.start();
	}

	protected abstract void onObject(Object o);

	public void onDisconnect(DisconnectCause cause)
	{
		try
		{
			this.close();
		}
		catch (IOException e)
		{
			logger.error("Failed to close.", e);
		}

		this.disconnectCause = cause;
	}

	public void sendCustomData(byte[] data) throws IOException
	{
		this.networkInterface.getOutputStream().write(data);
		this.networkInterface.getOutputStream().flush();
	}

	public void send(Object o)
	{
		logger.debug("Sending {} via {}", o, this.networkInterface);

		try
		{
			logger.debug("DataDump:\n{}", this.xStream.toXML(o));
			// this.xStream.toXML(o, this.networkInterface.getOutputStream());
			this.out.writeObject(o);
			this.out.flush();
		}
		catch (XStreamException e)
		{
			logger.error("Couldn't send message", e);
			onDisconnect(DisconnectCause.PROTOCOL_ERROR);
		}
		catch (IOException e)
		{
			logger.error("Couldn't send message", e);
			onDisconnect(DisconnectCause.LOST_CONNECTION);
		}
	}

	public DisconnectCause getDisconnectCause()
	{
		return this.disconnectCause;
	}

	public void close() throws IOException
	{
		if (!this.closing)
		{
			this.closing = true;
			this.thread.interrupt();
			this.networkInterface.close();
		}
	}
}
