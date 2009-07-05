package sc.sample.server;

import sc.api.plugins.IPlayer;
import sc.api.plugins.RescueableClientException;
import sc.api.plugins.TooManyPlayersException;
import sc.framework.plugins.RoundBasedGameInstance;
import sc.sample.shared.Board;
import sc.sample.shared.Move;
import sc.sample.shared.Player;

public class GameInstanceImpl extends RoundBasedGameInstance<PlayerImpl>
{
	private ServerBoard	board	= new ServerBoard();

	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public IPlayer onPlayerJoined() throws TooManyPlayersException
	{
		PlayerImpl newPlayer = new PlayerImpl(this.players.size() == 0 ? "X"
				: "O");
		this.players.add(newPlayer);
		return newPlayer;
	}

	@Override
	public void onPlayerLeft(IPlayer player)
	{
		this.players.remove(player);
	}

	@Override
	public void start()
	{
		super.start();
	}

	@Override
	protected boolean checkGameOverCondition()
	{
		return board.isGameOver();
	}

	@Override
	protected void onRoundBasedAction(IPlayer fromPlayer, Object data) throws RescueableClientException
	{
		if (data instanceof Move)
		{
			board.apply(resolve(fromPlayer), (Move) data);
			next();
		}
	}
	
	protected Player resolve(IPlayer player)
	{
		return ((PlayerImpl)player).getData();
	}
}
