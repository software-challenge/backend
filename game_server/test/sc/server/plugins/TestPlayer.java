package sc.server.plugins;

import java.util.LinkedList;
import java.util.List;

import sc.api.plugins.host.IPlayerListener;
import sc.framework.plugins.SimplePlayer;
import sc.shared.PlayerScore;
import sc.shared.ScoreCause;

public class TestPlayer extends SimplePlayer
{
	List<IPlayerListener>	listeners	= new LinkedList<IPlayerListener>();

	@Override
	public void addPlayerListener(IPlayerListener listener)
	{
		listeners.add(listener);
	}

	@Override
	public void removePlayerListener(IPlayerListener listener)
	{
		listeners.remove(listener);
	}

	public void requestMove()
	{
		TestTurnRequest request = new TestTurnRequest();
		
		for (IPlayerListener listener : listeners)
		{
			listener.onPlayerEvent(request);
		}
	}
	
	@Override
	public PlayerScore getScore()
	{
		return new PlayerScore(true);
	}
}
