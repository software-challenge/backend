package sc.framework.plugins;

import java.util.LinkedList;
import java.util.List;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGameListener;

public abstract class SimpleGameInstance<P extends SimplePlayer> implements IGameInstance
{
	protected List<IGameListener>	listeners	= new LinkedList<IGameListener>();
	protected List<P>				players		= new LinkedList<P>();

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

	protected void notifyListeners(Object o)
	{
		for (IGameListener listener : this.listeners)
		{
			// todo
		}
	}

	protected void notifyOnGameOver()
	{
		for (IGameListener listener : this.listeners)
		{
			listener.onGameOver();
		}
	}
}
