package sc.protocol;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.networking.FileSystemInterface;

import com.thoughtworks.xstream.XStream;

/**
 * A client that emulates a network connection but instead uses a file to read
 * it's replay from.
 * 
 * @author Marcel
 * 
 */
public final class ReplayClient extends XStreamClient
{
	private static Logger	logger			= LoggerFactory
													.getLogger(ReplayClient.class);
	List<MementoPacket>		mementi			= new LinkedList<MementoPacket>();
	List<StateListener>		listeners		= new LinkedList<StateListener>();
	MementoPacket			currentMemento	= null;

	public ReplayClient(XStream xstream, File file) throws IOException
	{
		super(xstream, new FileSystemInterface(file));
	}

	@Override
	protected void onObject(Object o)
	{
		if (o instanceof MementoPacket)
		{
			this.mementi.add((MementoPacket) o);
		}
		else
		{
			logger.info("Dropped unknown Object: " + o);
		}
	}

	public void addListener(StateListener listener)
	{
		this.listeners.add(listener);
	}

	public void removeListener(StateListener listener)
	{
		this.listeners.remove(listener);
	}

	public void next()
	{
		assertCurrent();
		int i = this.mementi.indexOf(this.currentMemento) + 1;

		if (i >= this.mementi.size())
		{
			i = this.mementi.size() - 1;
		}

		setCurrent(this.mementi.get(i));
	}

	public void previous()
	{
		assertCurrent();
		int i = this.mementi.indexOf(this.currentMemento) - 1;

		if (i < 0)
		{
			i = 0;
		}

		setCurrent(this.mementi.get(i));
	}

	public void pause()
	{
		// TODO:
	}

	public void unpause()
	{
		// TODO:
	}

	public boolean atEnd()
	{
		assertCurrent();
		return this.mementi.indexOf(this.currentMemento) > this.mementi.size();
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

			for (StateListener listener : this.listeners)
			{
				listener.onNewState(p.getState());
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
			if (this.mementi.size() > 0)
			{
				setCurrent(this.mementi.get(0));
			}
			else
			{
				throw new RuntimeException("No Memento available");
			}
		}
	}
}
