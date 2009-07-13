package sc.sample.server;

import sc.api.plugins.host.PlayerScore;
import sc.api.plugins.host.ScoreCause;
import sc.framework.plugins.SimplePlayer;
import sc.sample.shared.Player;

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
