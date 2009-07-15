package sc.protocol;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.networking.FileSystemInterface;
import sc.protocol.clients.IUpdateListener;
import sc.protocol.clients.ObservingClient;

import com.thoughtworks.xstream.XStream;

/**
 * A client that emulates a network connection but instead uses a file to read
 * it's replay from.
 * 
 * @author Marcel
 * 
 */
public final class ReplayClient extends XStreamClient implements IPollsHistory
{
	private static Logger			logger		= LoggerFactory
														.getLogger(ReplayClient.class);
	private List<IHistoryListener>	listeners	= new LinkedList<IHistoryListener>();

	public ReplayClient(XStream xstream, InputStream inputStream) throws IOException
	{
		super(xstream, new FileSystemInterface(inputStream));
		logger.info("Loading Replay from {}", inputStream);
	}

	@Override
	protected void onObject(Object o)
	{
		for (IHistoryListener listener : this.listeners)
		{
			listener.onNewState(null, o);
		}
	}

	@Override
	public void addListener(IHistoryListener listener)
	{
		this.listeners.add(listener);
	}

	@Override
	public void removeListener(IHistoryListener listener)
	{
		this.listeners.remove(listener);
	}
}
