package sc.protocol;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.networking.FileSystemInterface;
import sc.protocol.clients.IUpdateListener;

import com.thoughtworks.xstream.XStream;

/**
 * A client that emulates a network connection but instead uses a file to read
 * it's replay from.
 * 
 * @author Marcel
 * 
 */
public final class ReplayClient extends XStreamClient implements
		IControllableGame
{
	private static Logger				logger			= LoggerFactory
																.getLogger(ReplayClient.class);
	List<MementoPacket>					history			= new LinkedList<MementoPacket>();
	private final List<IUpdateListener>	listeners		= new LinkedList<IUpdateListener>();
	MementoPacket						currentMemento	= null;

	public ReplayClient(XStream xstream, File file) throws IOException
	{
		super(xstream, new FileSystemInterface(file));
	}

	@Override
	protected void onObject(Object o)
	{
		if (o instanceof MementoPacket)
		{
			this.history.add((MementoPacket) o);
		}
		else
		{
			logger.info("Dropped unknown Object: " + o);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sc.protocol.IControllableGame#next()
	 */
	public void next()
	{
		assertCurrent();
		int i = this.history.indexOf(this.currentMemento) + 1;

		if (i >= this.history.size())
		{
			i = this.history.size() - 1;
		}

		setCurrent(this.history.get(i));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sc.protocol.IControllableGame#previous()
	 */
	public void previous()
	{
		assertCurrent();
		int i = this.history.indexOf(this.currentMemento) - 1;

		if (i < 0)
		{
			i = 0;
		}

		setCurrent(this.history.get(i));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sc.protocol.IControllableGame#pause()
	 */
	public void pause()
	{
		// TODO:
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sc.protocol.IControllableGame#unpause()
	 */
	public void unpause()
	{
		// TODO:
	}

	public boolean atEnd()
	{
		assertCurrent();
		return this.history.indexOf(this.currentMemento) > this.history.size();
	}

	public Object getCurrentState()
	{
		assertCurrent();
		return this.currentMemento.getState();
	}

	private void setCurrent(MementoPacket p)
	{
		if (p != null)
		{
			this.currentMemento = p;

			for (IUpdateListener listener : this.listeners)
			{
				listener.onUpdate(this);
			}
		}
		else
		{
			throw new IllegalArgumentException("New value must not be null.");
		}
	}

	private void assertCurrent()
	{
		if (this.currentMemento == null)
		{
			if (this.history.size() > 0)
			{
				setCurrent(this.history.get(0));
			}
			else
			{
				throw new RuntimeException("No Memento available");
			}
		}
	}

	@Override
	public void removeListener(IUpdateListener u)
	{
		this.listeners.add(u);
	}

	@Override
	public void addListener(IUpdateListener u)
	{
		this.listeners.remove(u);
	}
}
