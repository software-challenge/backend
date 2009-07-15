package sc.server.plugins;

import java.util.List;

import sc.api.plugins.IPlayer;
import sc.api.plugins.exceptions.RescueableClientException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.framework.plugins.RoundBasedGameInstance;

public class TestGame extends RoundBasedGameInstance<TestPlayer>
{
	private TestGameState	state	= new TestGameState();

	public TestGame()
	{
		state.setController(this);
	}

	@Override
	protected void onRoundBasedAction(IPlayer fromPlayer, Object data)
			throws RescueableClientException
	{
		if (data instanceof TestMove)
		{
			int newSecret = ((TestMove) data).value;

			if (fromPlayer == players.get(0))
			{
				state.secret0 = newSecret;
			}
			else if (fromPlayer == players.get(1))
			{
				state.secret1 = newSecret;
			}
			else
			{
				throw new RuntimeException("Unknown Player");
			}
		}
		state.round++;
		next();
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
	protected boolean checkGameOverCondition()
	{
		return (this.state.round == 4);
	}

	public List<TestPlayer> getPlayers()
	{
		return this.players;
	}

	@Override
	protected Object getCurrentState()
	{
		return this.state;
	}

	@Override
	public void onPlayerLeft(IPlayer player)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public boolean ready()
	{
		return this.players.size() == TestPlugin.MAXIMUM_PLAYER_SIZE;
	}

	@Override
	protected void onNewTurn()
	{
		// TODO Auto-generated method stub
		
	}
}
