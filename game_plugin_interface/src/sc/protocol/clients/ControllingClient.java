package sc.protocol.clients;

import sc.protocol.IControllableGame;
import sc.protocol.LobbyClient;

public class ControllingClient extends ObservingClient implements
		IControllableGame
{
	public ControllingClient(LobbyClient client, String roomId)
	{
		super(client, roomId);
	}

	int			position	= 0;
	PlayMode	mode		= PlayMode.PAUSED;

	enum PlayMode
	{
		PLAYING, PAUSED
	}

	@Override
	protected void addObservation(Object observation)
	{
		super.addObservation(observation);
	}

	@Override
	public void next()
	{
		this.position = Math.min(this.position + 1, this.history.size() - 1);
	}

	@Override
	public void pause()
	{
		this.mode = PlayMode.PAUSED;
	}

	@Override
	public void previous()
	{
		this.position = Math.max(this.position - 1, 0);
	}

	@Override
	public void unpause()
	{
		this.mode = PlayMode.PLAYING;
	}

}
