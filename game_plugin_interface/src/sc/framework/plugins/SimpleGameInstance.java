package sc.framework.plugins;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IPlayer;
import sc.api.plugins.host.IGameListener;
import sc.shared.PlayerScore;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

public abstract class SimpleGameInstance<P extends SimplePlayer> implements
		IGameInstance
{
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
			listener.onGameOver(map);
		}
	}

	protected void notifyOnNewState(Object mementoState)
	{
		for (IGameListener listener : this.listeners)
		{
			listener.onStateChanged(mementoState);
		}
	}
}
