package sc.networking.clients;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.networking.INetworkInterface;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

public abstract class XStreamClient
{
	private static Logger				logger			= LoggerFactory
																.getLogger(XStreamClient.class);
	private final INetworkInterface		networkInterface;
	private final ObjectOutputStream	out;
	ObjectInputStream					in;
	private final Thread				thread;
	private DisconnectCause				disconnectCause	= DisconnectCause.NOT_DISCONNECTED;
	protected final XStream				xStream;
	private boolean						closed			= false;
	private boolean						ready			= false;
	private final Object				readyLock		= new Object();

	public enum DisconnectCause
	{
		REGULAR, PROTOCOL_ERROR, LOST_CONNECTION, TIMEOUT, UNKNOWN,

		NOT_DISCONNECTED,
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

		this.xStream = xstream;
		this.xStream.setMode(XStream.ID_REFERENCES);
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
					receiveThread();
				}
				catch (Exception e)
				{
					this.threadLogger.error("ReceiveThread caused an exception.", e);
				}
			}
		});
		this.thread.setName("XStreamClient Reader");
		this.thread.start();
	}

	protected abstract void onObject(Object o);

	/**
	 * used internally by a ReceiveThread - all Exceptions should be handled.
	 */
	public void receiveThread() throws Exception
	{
		try
		{
			XStreamClient.this.in = this.xStream
					.createObjectInputStream(this.networkInterface
							.getInputStream());

			synchronized (this.readyLock)
			{
				if (!isReady())
				{
					this.readyLock.wait();
				}
			}

			while (!Thread.interrupted())
			{
				Object o = XStreamClient.this.in.readObject();
				logger.debug("Received {} via {}", o, this.networkInterface);
				onObject(o);
			}

			handleDisconnect(DisconnectCause.DISCONNECTED);
		}
		catch (EOFException e)
		{
			handleDisconnect(DisconnectCause.REGULAR, e);
		}
		catch (IOException e)
		{
			handleDisconnect(DisconnectCause.LOST_CONNECTION, e);
		}
		catch (ClassNotFoundException e)
		{
			handleDisconnect(DisconnectCause.PROTOCOL_ERROR, e);
		}
		catch (XStreamException e)
		{
			Throwable exceptionCause = e.getCause();
			if (exceptionCause != null)
			{
				if (exceptionCause instanceof SocketException)
				{
					handleDisconnect(DisconnectCause.LOST_CONNECTION, e);
				}
				else if (exceptionCause instanceof EOFException)
				{
					handleDisconnect(DisconnectCause.LOST_CONNECTION, e);
				}
				else if (exceptionCause instanceof IOException
						&& exceptionCause.getCause() != null
						&& exceptionCause.getCause() instanceof InterruptedException)
				{
					handleDisconnect(DisconnectCause.LOST_CONNECTION, e);
				}
				else
				{
					handleDisconnect(DisconnectCause.PROTOCOL_ERROR, e);
				}
			}
			else
			{
				handleDisconnect(DisconnectCause.PROTOCOL_ERROR, e);
			}
		}
		catch (Exception e)
		{
			logger.error("Unknown Communication Error", e);
			handleDisconnect(DisconnectCause.UNKNOWN, e);
		}

		return;
	}

	public void sendCustomData(String data) throws IOException
	{
		sendCustomData(data.getBytes("utf-8"));
	}

	public void sendCustomData(byte[] data) throws IOException
	{
		logger.warn("Sending Custom data (size={})", data.length);

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

		if (isClosed())
		{
			throw new IllegalStateException("Writing on a closed xStream.");
		}

		logger.debug("Sending {} via {}", o, this.networkInterface);

		try
		{
			logger.debug("DataDump:\n{}", this.xStream.toXML(o));
			this.out.writeObject(o);
			this.out.flush();
		}
		catch (XStreamException e)
		{
			handleDisconnect(DisconnectCause.PROTOCOL_ERROR, e);
		}
		catch (IOException e)
		{
			handleDisconnect(DisconnectCause.LOST_CONNECTION, e);
		}
	}

	protected final void handleDisconnect(DisconnectCause cause)
	{
		handleDisconnect(cause, null);
	}

	protected final void handleDisconnect(DisconnectCause cause,
			Throwable exception)
	{
		if (exception != null)
		{
			logger.warn("Client disconnected (Cause: " + cause
					+ ", Exception: " + exception + ")");
		}
		else
		{
			logger.info("Client disconnected (Cause: {})", cause);
		}

		this.disconnectCause = cause;

		try
		{
			this.close();
		}
		catch (Exception e)
		{
			logger.error("Failed to close.", e);
		}

		onDisconnect(cause);
	}

	protected void onDisconnect(DisconnectCause cause)
	{
		// callback
	}

	public DisconnectCause getDisconnectCause()
	{
		return this.disconnectCause;
	}

	public synchronized void close()
	{
		if (!isClosed())
		{
			this.closed = true;

			// unlock waiting threads
			synchronized (this.readyLock)
			{
				this.readyLock.notifyAll();
			}

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
			catch (Exception e)
			{
				logger.error("Failed to close OUT", e);
			}

			try
			{
				if (this.in != null)
				{
					this.in.close();
				}
			}
			catch (Exception e)
			{
				logger.error("Failed to close IN", e);
			}

			try
			{
				this.networkInterface.close();
			}
			catch (Exception e)
			{
				logger.warn("Failed to close NetworkInterface", e);
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
