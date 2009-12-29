package sc.protocol.responses;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import sc.api.plugins.IPlayer;

@XStreamAlias(value="paused")
public class GamePausedEvent
{
	private final IPlayer	nextPlayer;

	public GamePausedEvent(IPlayer nextPlayer)
	{
		this.nextPlayer = nextPlayer;
	}

	public IPlayer getNextPlayer()
	{
		return this.nextPlayer;
	}
}
