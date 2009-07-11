package sc.protocol.clients;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import sc.protocol.ErrorResponse;
import sc.protocol.LobbyClient;
import sc.protocol.responses.PrepareGameResponse;

public class ObservingClient extends SingleRoomClient
{
	public ObservingClient(LobbyClient client, String roomId)
	{
		super(client, roomId);
	}

	protected final List<Object>	history	= new LinkedList<Object>();

	public void close()
	{
		this.history.clear();
	}

	protected void addObservation(Object observation)
	{
		this.history.add(observation);
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
}
