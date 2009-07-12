package sc.protocol.clients;

import sc.protocol.LobbyClient;
import sc.protocol.requests.PauseGameRequest;
import sc.protocol.requests.StepRequest;

public class ControllingClient extends ObservingClient
{
	public ControllingClient(LobbyClient client, String roomId)
	{
		super(client, roomId);
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
		if(atEnd()) {
			this.client.send(new StepRequest(this.roomId));			
		}
		super.next();
	}
}
