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
	public PlayerScore getScore()
	{
		return new PlayerScore(ScoreCause.REGULAR, 1, 0);
	}
}
