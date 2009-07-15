package sc.protocol.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.IPlayer;
import sc.protocol.IAdministrativeListener;
import sc.protocol.LobbyClient;
import sc.protocol.requests.PauseGameRequest;
import sc.protocol.requests.StepRequest;

public class ControllingClient extends ObservingClient implements
		IAdministrativeListener
{
	private static final Logger	logger			= LoggerFactory
														.getLogger(ControllingClient.class);
	final LobbyClient			client;
	private boolean				allowOneStep	= false;
	private boolean				pauseHitReceived;

	public ControllingClient(LobbyClient client, String roomId)
	{
		super(client, roomId);
		this.client = client;
		client.addListener((IAdministrativeListener) this);
	}

	@Override
	protected void addObservation(Object observation)
	{
		super.addObservation(observation);

		if (this.allowOneStep)
		{
			changePosition(+1);
			this.allowOneStep = false;
		}
	}

	@Override
	public void pause()
	{
		if (!this.client.isClosed())
		{
			this.client.send(new PauseGameRequest(this.roomId, true));
		}
		super.pause();
	}

	@Override
	public void unpause()
	{
		this.pauseHitReceived = false;
		this.gotoEnd();

		if (!this.client.isClosed())
		{
			this.client.send(new PauseGameRequest(this.roomId, false));
		}

		super.unpause();
	}

	private void gotoEnd()
	{
		this.setPosition(this.getHistory().size() - 1);
	}

	@Override
	public void next()
	{
		if (atEnd())
		{
			this.pauseHitReceived = false;

			if (!this.client.isClosed())
			{
				this.client.send(new StepRequest(this.roomId));
				this.allowOneStep = true;
			}
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

		if (isPaused() && atEnd())
		{
			return this.pauseHitReceived;
		}

		return super.hasNext();
	}

	@Override
	public void cancel()
	{
		this.pauseHitReceived = false;

		if (!isGameOver())
		{
			if (!this.client.isClosed())
			{
				this.client.send(new CancelRequest(this.roomId));
			}
		}

		super.cancel();
	}

	@Override
	public void onGamePaused(String roomId, IPlayer nextPlayer)
	{
		logger.info("A PAUSE HIT was detected.");
		
		this.pauseHitReceived = true;
	}
}
