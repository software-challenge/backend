package sc.server.plugins;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGameListener;
import sc.api.plugins.IPlayer;
import sc.api.plugins.TooManyPlayersException;

public class TestGame implements IGameInstance
{
	private List<IGameListener>	listeners		= new LinkedList<IGameListener>();
	private List<TestPlayer>	players			= new ArrayList<TestPlayer>(2);
	private int					activePlayerId	= 0;

	@Override
	public void onAction(IPlayer fromPlayer, Object data)
	{
		if (fromPlayer.equals(players.get(activePlayerId)))
		{
			if (data instanceof TestMove)
			{
				nextRound();
				if (activePlayerId == 0)
				{
					notifyOnGameOver();
				}
				else
				{
					notifyActivePlayer();
				}
			}
			else
			{
				throw new RuntimeException("aaa");
			}
		}
		else
		{
			throw new RuntimeException("aaa");
		}
	}

	private void notifyOnGameOver()
	{
		for (IGameListener listener : listeners)
		{
			listener.onGameOver();
		}
	}

	@Override
	public void addGameListener(IGameListener listener)
	{
		listeners.add(listener);
	}

	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public IPlayer onPlayerJoined() throws TooManyPlayersException
	{
		if (players.size() < 2)
		{
			TestPlayer player = new TestPlayer();
			players.add(player);
			return player;
		}
		else
		{
			throw new TooManyPlayersException();
		}
	}

	@Override
	public void onPlayerLeft(IPlayer player)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeGameListener(IGameListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void start()
	{
		if (this.players.size() == 2)
		{
			notifyActivePlayer();
		}
	}

	public void notifyActivePlayer()
	{
		players.get(this.activePlayerId).requestMove();
	}

	public void nextRound()
	{
		this.activePlayerId = (this.activePlayerId + 1) % 2;
	}
}
