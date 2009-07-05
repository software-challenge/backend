package sc.sample.server;

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
}
