package sc.protocol.responses;

import sc.api.plugins.IPlayer;

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
