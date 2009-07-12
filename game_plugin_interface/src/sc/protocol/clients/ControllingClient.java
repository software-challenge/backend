package sc.protocol.clients;

import sc.protocol.LobbyClient;
import sc.protocol.requests.PauseGameRequest;
import sc.protocol.requests.StepRequest;

public class ControllingClient extends ObservingClient
{
	final LobbyClient	client;

	public ControllingClient(LobbyClient client, String roomId)
	{
		super(client, roomId);
		this.client = client;
	}

	@Override
	public void pause()
	{
		this.client.send(new PauseGameRequest(this.roomId, true));
		super.pause();
	}

	@Override
	public void unpause()
	{
		this.client.send(new PauseGameRequest(this.roomId, false));
		super.unpause();
	}

	@Override
	public void next()
	{
		if (atEnd())
		{
			this.client.send(new StepRequest(this.roomId));
		}

		super.next();
	}

	@Override
	public boolean hasNext()
	{
		if (isGameOver())
		{
			return super.hasNext();
		}

		return isPaused();
	}
	
	@Override
	public void cancel()
	{
		if(!isGameOver()) 
		{
			this.client.send(new CancelRequest(this.roomId));
		}
		
		super.cancel();
	}
}
