package sc.networking.clients;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

import sc.networking.INetworkInterface;
import sc.protocol.responses.CloseConnection;

public abstract class XStreamClient
{
	private static Logger				logger			= LoggerFactory
																.getLogger(XStreamClient.class);
	private final INetworkInterface		networkInterface;
	private final ObjectOutputStream	out;
	private ObjectInputStream					in;
	private final Thread				thread;
	private DisconnectCause				disconnectCause	= DisconnectCause.NOT_DISCONNECTED;
	protected final XStream				xStream;
	private boolean						closed			= false;
	private boolean						ready			= false;
	private final Object				readyLock		= new Object();

	public enum DisconnectCause
	{
		// default state:
		NOT_DISCONNECTED,
		// disconnected because CloseConnection was received:
		RECEIVED_DISCONNECT,
		// disconnected from this side:
		DISCONNECTED,
		// error conditions:
		PROTOCOL_ERROR, LOST_CONNECTION, TIMEOUT, UNKNOWN

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
				while (!isReady())
				{
					this.readyLock.wait();
				}
			}

			while (!Thread.interrupted())
			{
				Object o = XStreamClient.this.in.readObject();
				logger.debug("Client " + XStreamClient.this +": Received " + o + " via " + this.networkInterface + "\nDataDump:\n{}", this.xStream.toXML(o));
				if (o instanceof CloseConnection) {
					handleDisconnect(DisconnectCause.RECEIVED_DISCONNECT);
					break; // stop receiver thread
				} else {
					onObject(o);
				}
			}
		}
		catch (EOFException e)
		{
			// The server closed the connection. This should not happen while
			// the client is still reading. The server should send a
			// CloseConnection message before, giving the client the chance to
			// close the connection regularly.
			handleDisconnect(DisconnectCause.LOST_CONNECTION, e);
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

		logger.debug("Client "+ this + ": Sending " + o + " via " + this.networkInterface + "\nDataDump:\n{}", this.xStream.toXML(o));

		try
		{
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
			logger.warn("Client "+ this + " disconnected (Cause: " + cause
					+ ", Exception: " + exception + ")");
		}
		else
		{
			logger.info("Client "+ this + " disconnected (Cause: {})", cause);
		}

		this.disconnectCause = cause;

		try
		{
			close();
		}
		catch (Exception e)
		{
			logger.error("Failed to close.", e);
		}

		onDisconnect(cause);
	}

	protected void onDisconnect(DisconnectCause cause)
	{
	}

	public DisconnectCause getDisconnectCause()
	{
		return this.disconnectCause;
	}

	public void stop() {
		stopReceiver();
		this.disconnectCause = DisconnectCause.DISCONNECTED;
		// this side caused disconnect, notify other side
		send(new CloseConnection());
		close();
	}

	protected synchronized void stopReceiver() {
		assert this.thread != Thread.currentThread();
		// unlock waiting threads
		synchronized (this.readyLock)
		{
		  this.readyLock.notifyAll();
		}

		if (this.thread != null)
		{
			this.thread.interrupt();
		}
	}

	protected synchronized void close()
	{
		if (!isClosed())
		{
			this.closed = true;

			stopReceiver();

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
		} else
		{
			logger.warn("Reclosing an already closed stream");
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
