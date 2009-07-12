package sc.framework.plugins;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import sc.api.plugins.IPlayer;
import sc.api.plugins.host.IPlayerListener;
import sc.api.plugins.host.PlayerScore;
import sc.framework.plugins.protocol.MoveRequest;

public abstract class SimplePlayer implements IPlayer
{
	public static final Logger logger = LoggerFactory.getLogger(SimplePlayer.class);
	
	@XStreamOmitField
	private List<IPlayerListener>	listeners	= new LinkedList<IPlayerListener>();

	@Override
	public void addPlayerListener(IPlayerListener listener)
	{
		this.listeners.add(listener);
	}

	@Override
	public void removePlayerListener(IPlayerListener listener)
	{
		this.listeners.remove(listener);
	}

	public void notifyListeners(Object o)
	{
		for (IPlayerListener listener : this.listeners)
		{
			listener.onPlayerEvent(o);
		}
	}

	public void requestMove()
	{
		MoveRequest request = new MoveRequest();

		for (IPlayerListener listener : this.listeners)
		{
			listener.onPlayerEvent(request);
		}
		
		logger.debug("Move requested.");
	}
	
	public abstract PlayerScore getScore();
}
