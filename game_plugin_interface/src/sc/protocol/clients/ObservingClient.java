package sc.protocol.clients;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sc.protocol.IControllableGame;
import sc.protocol.IHistoryListener;
import sc.protocol.IPollsHistory;
import sc.protocol.ReplayClient;

import com.thoughtworks.xstream.XStream;

public class ObservingClient implements IControllableGame, IHistoryListener
{
	public ObservingClient(XStream xStream, File file) throws IOException
	{
		this(new ReplayClient(xStream, file), null);
		this.mode = PlayMode.PAUSED;
		this.poller.start();
	}

	public ObservingClient(IPollsHistory client, String roomId)
	{
		this.poller = client;
		this.roomId = roomId;
		client.addListener(this);
	}

	protected final IPollsHistory		poller;

	protected final String				roomId;

	private boolean						gameOver	= false;

	private final List<Object>			history		= new LinkedList<Object>();

	private final List<IUpdateListener>	listeners	= new LinkedList<IUpdateListener>();

	protected int						position	= 0;

	protected PlayMode					mode		= PlayMode.PAUSED;

	enum PlayMode
	{
		PLAYING, PAUSED
	}

	protected void addObservation(Object observation)
	{
		this.history.add(observation);

		if (this.mode == PlayMode.PLAYING)
		{
			setPosition(this.history.size() - 1);
		}
	}

	@Override
	public void onNewState(String roomId, Object state)
	{
		if (this.roomId.equals(roomId))
		{
			addObservation(state);
		}
	}

	protected void notifyOnUpdate()
	{
		for (IUpdateListener listener : this.listeners)
		{
			listener.onUpdate(this);
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

	@Override
	public void next()
	{
		this.position = Math.min(this.position + 1, this.history.size() - 1);
	}

	@Override
	public void pause()
	{
		this.mode = PlayMode.PAUSED;
	}

	@Override
	public void previous()
	{
		if (this.mode == PlayMode.PLAYING)
		{
			pause();
		}
		this.setPosition(Math.max(this.position - 1, 0));
	}

	@Override
	public void unpause()
	{
		this.mode = PlayMode.PLAYING;
		this.setPosition(this.history.size() - 1);
	}

	private void setPosition(int i)
	{
		this.position = i;
		notifyOnUpdate();
	}

	public Object getCurrentState()
	{
		if (this.history.size() == 0)
		{
			return null;
		}

		return this.history.get(this.position);
	}

	public boolean atEnd()
	{
		return this.position >= this.history.size() - 1;
	}

	public List<Object> getHistory()
	{
		return Collections.unmodifiableList(this.history);
	}

	@Override
	public boolean hasNext()
	{
		return (this.position + 1) < this.history.size();
	}

	@Override
	public boolean hasPrevious()
	{
		return this.position > 0;
	}

	@Override
	public boolean isPaused()
	{
		return this.mode == PlayMode.PAUSED;
	}

	public boolean isGameOver()
	{
		return this.gameOver;
	}

	public void close()
	{
		this.history.clear();
	}

	@Override
	public void onGameOver(String roomId, Object o)
	{
		// TODO:
	}

	@Override
	public void cancel()
	{
		// TODO:
	}
}
