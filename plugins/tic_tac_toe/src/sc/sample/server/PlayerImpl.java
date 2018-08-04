package sc.sample.server;

import sc.framework.plugins.AbstractPlayer;
import sc.sample.shared.Player;
import sc.shared.PlayerScore;
import sc.shared.ScoreCause;

public class PlayerImpl extends AbstractPlayer
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

	public PlayerScore getScore()
	{
		return new PlayerScore(ScoreCause.REGULAR, 1, 0);
	}
}
