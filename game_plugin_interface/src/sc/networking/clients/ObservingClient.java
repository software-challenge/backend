package sc.networking.clients;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.thoughtworks.xstream.XStream;

public class ObservingClient implements IControllableGame, IHistoryListener
{
	public ObservingClient(XStream xStream, InputStream inputStream)
			throws IOException
	{
		this(new ReplayClient(xStream, inputStream), null);
		this.replay = true;
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

	private static final Logger			logger		= LoggerFactory
															.getLogger(ObservingClient.class);

	private boolean						replay		= false;

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
		boolean firstObservation = this.history.isEmpty();

		this.history.add(observation);

		if (this.mode == PlayMode.PLAYING || firstObservation)
		{
			setPosition(this.history.size() - 1);
		}
	}

	@Override
	public void onNewState(String roomId, Object state)
	{
		if (isAffected(roomId))
		{
			addObservation(state);
		}
	}

	private boolean isAffected(String roomId)
	{
		return this.replay || this.roomId.equals(roomId);
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
		this.listeners.remove(u);
	}

	@Override
	public void addListener(IUpdateListener u)
	{
		this.listeners.add(u);
		u.onUpdate(this);
	}

	@Override
	public void next()
	{
		this.changePosition(+1);
	}

	@Override
	public void pause()
	{
		this.mode = PlayMode.PAUSED;
		notifyOnUpdate();
	}

	@Override
	public void previous()
	{
		if (!isPaused())
		{
			pause();
		}

		this.changePosition(-1);
	}

	protected void changePosition(int i)
	{
		this.setPosition(this.getPosition() + i);
	}

	private int getPosition()
	{
		return this.position;
	}

	@Override
	public void unpause()
	{
		this.mode = PlayMode.PLAYING;
		if (this.replay)
		{
			next();
			// TODO: start a thread which increments automatically
		}
		else
		{
			this.setPosition(this.history.size() - 1);
		}
	}

	protected void setPosition(int i)
	{
		logger.debug("Setting Position to {}", i);
		this.position = Math.max(0, Math.min(this.history.size() - 1, i));
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
	
	@Override
	public boolean isAtStart()
	{
		return this.getPosition() == 0;
	}

	@Override
	public boolean isAtEnd()
	{
		return this.getPosition() >= this.history.size() - 1;
	}

	public List<Object> getHistory()
	{
		return Collections.unmodifiableList(this.history);
	}

	@Override
	public boolean hasNext()
	{
		return (this.getPosition() + 1) < this.history.size();
	}

	@Override
	public boolean hasPrevious()
	{
		return this.getPosition() > 0;
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

	@Override
	public void goToFirst()
	{
		this.setPosition(0);
	}

	@Override
	public void goToLast()
	{
		this.setPosition(getHistory().size() - 1);	
	}
}
