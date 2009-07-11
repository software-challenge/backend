package sc.sample.client;

import java.io.IOException;

import sc.protocol.ErrorResponse;
import sc.protocol.ILobbyClientListener;
import sc.protocol.LobbyClient;
import sc.protocol.responses.PrepareGameResponse;
import sc.sample.server.GamePluginImpl;
import sc.sample.shared.Board;
import sc.sample.shared.GameState;
import sc.sample.shared.Move;

public class SimpleClient implements ILobbyClientListener
{
	private LobbyClient	client;
	private GameState	state;

	public SimpleClient() throws IOException
	{
		this.client = new LobbyClient();
		this.client.addListener(this);
	}

	@Override
	public void onRoomMessage(String roomId, Object data)
	{
		for (int x = 0; x < Board.WIDTH; x++)
		{
			for (int y = 0; y < Board.HEIGHT; y++)
			{
				if (state.board.getOwner(x, y) == null)
				{
					client.sendMessageToRoom(roomId, new Move(x, y));
					return;
				}
			}
		}

		throw new RuntimeException("Couldn't find a valid move.");
	}

	@Override
	public void onError(ErrorResponse response)
	{
		System.err.println(response);
	}

	@Override
	public void onNewState(String roomId, Object state)
	{
		this.state = (GameState) state;
	}

	public void joinAnyGame()
	{
		client.joinAnyGame(GamePluginImpl.PLUGIN_UUID);
	}

	public void joinPreparedGame(String reservation)
	{
		client.joinPreparedGame(reservation);
	}

	@Override
	public void onGameJoined(String roomId)
	{

	}

	@Override
	public void onGameLeft(String roomId)
	{
		System.out.println("Game is over. Good night.");

		try
		{
			this.client.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onGamePrepared(PrepareGameResponse response)
	{

	}
}
