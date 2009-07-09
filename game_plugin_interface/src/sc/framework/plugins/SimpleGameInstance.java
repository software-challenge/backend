package sc.framework.plugins;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IPlayer;
import sc.api.plugins.host.IGameListener;
import sc.api.plugins.host.IPlayerScore;

public abstract class SimpleGameInstance<P extends SimplePlayer> implements
		IGameInstance
{
	protected final List<IGameListener>	listeners	= new LinkedList<IGameListener>();
	protected final List<P>				players		= new LinkedList<P>();

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

	protected void notifyOnGameOver()
	{
		for (IGameListener listener : this.listeners)
		{
			listener.onGameOver(new HashMap<IPlayer, IPlayerScore>());
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
