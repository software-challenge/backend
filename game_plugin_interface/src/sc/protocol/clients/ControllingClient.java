package sc.protocol.clients;

import sc.protocol.LobbyClient;
import sc.protocol.clients.ObservingClient.PlayMode;
import sc.protocol.requests.PauseGameRequest;
import sc.protocol.requests.StepRequest;

public class ControllingClient extends ObservingClient
{
	final LobbyClient	client;
	private boolean	allowOneStep = false;

	public ControllingClient(LobbyClient client, String roomId)
	{
		super(client, roomId);
		this.client = client;
	}
	
	@Override
	protected void addObservation(Object observation)
	{
		super.addObservation(observation);
		
		if(this.allowOneStep)
		{
			changePosition(+1);
			this.allowOneStep = false;
		}
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
			this.allowOneStep = true;
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
			if(!this.client.isClosed()) {
				this.client.send(new CancelRequest(this.roomId));				
			}
		}
		
		super.cancel();
	}
}
