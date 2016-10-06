package sc.framework.plugins;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IPlayer;
import sc.api.plugins.host.IGameListener;
import sc.shared.PlayerScore;

public abstract class SimpleGameInstance<P extends SimplePlayer> implements
		IGameInstance
{
	public static final Logger			logger		= LoggerFactory
															.getLogger(SimpleGameInstance.class);

	@XStreamOmitField
	protected final List<IGameListener>	listeners	= new LinkedList<IGameListener>();

	@XStreamImplicit(itemFieldName = "player")
	protected final List<P>				players		= new ArrayList<P>();

	@Override
	public void addGameListener(IGameListener listener)
	{
		this.listeners.add(listener);
	}

	@Override
	public void removeGameListener(IGameListener listener)
	{
		this.listeners.remove(listener);
	}

	protected void notifyOnGameOver(Map<IPlayer, PlayerScore> map)
	{
		for (IGameListener listener : this.listeners)
		{
			try
			{
				listener.onGameOver(map);
			}
			catch (Exception e)
			{
				logger.error("GameOver Notification caused an exception.", e);
			}
		}
	}

	protected void notifyOnNewState(Object mementoState)
	{
		for (IGameListener listener : this.listeners)
		{
			logger.debug("notifying {} about new game state", listener);
			try
			{
				listener.onStateChanged(mementoState);
			}
			catch (Exception e)
			{
				logger.error("NewState Notification caused an exception.", e);
			}
		}
	}


}
