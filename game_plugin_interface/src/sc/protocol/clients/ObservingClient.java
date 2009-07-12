package sc.protocol.clients;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import sc.protocol.ErrorResponse;
import sc.protocol.IControllableGame;
import sc.protocol.LobbyClient;
import sc.protocol.responses.PrepareGameResponse;

public class ObservingClient extends SingleRoomClient implements
		IControllableGame
{
	public ObservingClient(LobbyClient client, String roomId)
	{
		super(client, roomId);
	}

	protected final List<Object>		history		= new LinkedList<Object>();

	private final List<IUpdateListener>	listeners	= new LinkedList<IUpdateListener>();

	public void close()
	{
		this.history.clear();
	}

	int			position	= 0;
	PlayMode	mode		= PlayMode.PAUSED;

	enum PlayMode
	{
		PLAYING, PAUSED
	}

	protected void addObservation(Object observation)
	{
		this.history.add(observation);

		if (this.mode == PlayMode.PLAYING)
		{
			setPosition(this.history.size() - 1);
		}
	}

	public void saveReplayTo(String gameId, OutputStream out)
			throws IOException
	{
		ObjectOutputStream objectOut = this.client.getXStream()
				.createObjectOutputStream(out);

		for (Object state : this.history)
		{
			objectOut.writeObject(state);
		}

		objectOut.flush();
	}

	@Override
	public void onError(ErrorResponse error)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onGameJoined(String roomId)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onGameLeft(String roomId)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onGamePrepared(PrepareGameResponse response)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onNewState(String roomId, Object state)
	{
		if (this.roomId.equals(roomId))
		{
			addObservation(state);
		}
	}

	@Override
	public void onRoomMessage(String roomId, Object data)
	{

	}

	protected void notifyOnUpdate()
	{
		for (IUpdateListener listner : this.listeners)
		{
			listner.onUpdate(this);
		}
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
		if (this.mode == PlayMode.PLAYING)
		{
			pause();
		}
		this.setPosition(Math.max(this.position - 1, 0));
	}

	@Override
	public void unpause()
	{
		this.mode = PlayMode.PLAYING;
		this.setPosition(this.history.size() - 1);
	}

	private void setPosition(int i)
	{
		this.position = i;
		notifyOnUpdate();
	}

	public Object getCurrentState()
	{
		if (this.history.size() == 0)
		{
			return null;
		}

		return this.history.get(this.position);
	}

	public boolean atEnd()
	{
		return this.position >= this.history.size() - 1;
	}
}
