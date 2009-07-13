package sc.sample.server;

import sc.framework.plugins.SimplePlayer;
import sc.sample.shared.Player;
import sc.shared.PlayerScore;
import sc.shared.ScoreCause;

public class PlayerImpl extends SimplePlayer
{
	private Player	data;

	public PlayerImpl(String symbol)
	{
		data = new Player(symbol);
	}

	public Player getData()
	{
		return data;
	}

	@Override
	public PlayerScore getScore(ScoreCause cause)
	{
		return new PlayerScore(cause, 1, 0);
	}
}
