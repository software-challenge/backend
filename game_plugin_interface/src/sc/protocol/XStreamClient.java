package sc.protocol;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.networking.INetworkInterface;
import sun.awt.windows.ThemeReader;

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
	protected final XStream				xStream;
	private boolean						closed			= false;
	private boolean						ready			= false;
	private final Object				readyLock		= new Object();

	public enum DisconnectCause
	{
		REGULAR, PROTOCOL_ERROR, LOST_CONNECTION, TIMEOUT, UNKNOWN,

		/**
		 * Connection was closed from this side.
		 */
		DISCONNECTED
	}

	public boolean isReady()
	{
		return this.ready;
	}

	public void start()
	{
		synchronized (this.readyLock)
		{
			if (!this.ready)
			{
				this.ready = true;
				this.readyLock.notifyAll();
			}
		}
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

		final Object theReadyLock = this.readyLock;
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

					synchronized (theReadyLock)
					{
						if (!isReady())
						{
							theReadyLock.wait();
						}
					}

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
					if (e.getCause() != null)
					{
						if (e.getCause() instanceof SocketException)
						{
							onDisconnect(DisconnectCause.LOST_CONNECTION);
						}
						else if (e.getCause() instanceof EOFException)
						{
							onDisconnect(DisconnectCause.LOST_CONNECTION);
						}
						else if (e.getCause() instanceof IOException
								&& e.getCause().getCause() instanceof InterruptedException)
						{
							onDisconnect(DisconnectCause.LOST_CONNECTION);
						}
						else
						{
							onDisconnect(DisconnectCause.PROTOCOL_ERROR);
							this.threadLogger.error(
									"Client violated against the protocol.", e);
						}
					}
					else
					{
						onDisconnect(DisconnectCause.PROTOCOL_ERROR);
						this.threadLogger.error(
								"Client violated against the protocol.", e);
					}
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
		this.thread.setName("XStreamClient Reader");
		this.thread.start();
	}

	protected abstract void onObject(Object o);

	public void onDisconnect(DisconnectCause cause)
	{
		logger.info("Client {} disconnected. Cause: {}", this, cause);

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
		if (!isReady())
		{
			throw new IllegalStateException(
					"Please call start() before sending any messages.");
		}

		if (this.closed)
		{
			throw new IllegalStateException("Writing on a closed xStream.");
		}

		logger.debug("Sending {} via {}", o, this.networkInterface);

		try
		{
			// logger.debug("DataDump:\n{}", this.xStream.toXML(o));
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

	public synchronized void close() throws IOException
	{
		if (!this.closed)
		{
			this.closed = true;

			// unlock waiting threads
			synchronized (this.readyLock)
			{
				this.readyLock.notifyAll();
			}

			//
			if (this.thread != null)
			{
				this.thread.interrupt();
			}

			try
			{
				if (this.out != null)
				{
					this.out.close();
				}
			}
			finally
			{
				try
				{
					if (this.in != null)
					{
						this.in.close();
					}
				}
				finally
				{
					this.networkInterface.close();
				}
			}
		}
	}

	public XStream getXStream()
	{
		return this.xStream;
	}

	public boolean isClosed()
	{
		return this.closed;
	}
}
