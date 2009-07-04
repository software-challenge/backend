package sc.server.plugins;

import java.util.ArrayList;
import java.util.List;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGameListener;
import sc.api.plugins.IPlayer;
import sc.api.plugins.TooManyPlayersException;

public class TestGame implements IGameInstance
{
	private List<TestPlayer>	players	= new ArrayList<TestPlayer>(2);

	@Override
	public void actionReceived(IPlayer fromPlayer, Object data)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void addGameListener(IGameListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public IPlayer playerJoined() throws TooManyPlayersException
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
	public void playerLeft(IPlayer player)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeGameListener(IGameListener listener)
	{
		// TODO Auto-generated method stub

	}

}
