package sc.framework.plugins;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IPlayer;
import sc.api.plugins.host.IGameListener;
import sc.api.plugins.host.PlayerScore;

public abstract class SimpleGameInstance<P extends SimplePlayer> implements
		IGameInstance
{
	@XStreamOmitField
	protected final List<IGameListener>	listeners	= new LinkedList<IGameListener>();
	
	@XStreamOmitField
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
		Map<IPlayer, PlayerScore> map = new HashMap<IPlayer, PlayerScore>();
		
		for(P player : this.players)
		{
			map.put(player, player.getScore());
		}
		
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
