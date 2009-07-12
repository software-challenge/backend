package sc.plugin2010;

import java.io.IOException;
import java.util.Arrays;

import com.thoughtworks.xstream.XStream;

import sc.api.plugins.GameResult;
import sc.framework.plugins.protocol.MoveRequest;
import sc.plugin2010.Move.MoveTyp;
import sc.protocol.ErrorResponse;
import sc.protocol.ILobbyClientListener;
import sc.protocol.LobbyClient;
import sc.protocol.responses.PrepareGameResponse;

public class TestClient implements ILobbyClientListener
{
	private LobbyClient	client;

	public TestClient() throws IOException
	{
		client = new LobbyClient(new XStream(), Arrays.asList(Player.class,
				PlayerUpdated.class, Move.class, Board.class,
				BoardUpdated.class));
		this.client.addListener(this);
		this.client.start();
	}

	private Board				board;
	private Player				own;

	@Override
	public void onRoomMessage(String roomId, Object data)
	{
		if (data instanceof BoardUpdated)
		{
			this.board = ((BoardUpdated) data).getBoard();
		}
		else if (data instanceof PlayerUpdated)
		{
			if (((PlayerUpdated) data).isOwnPlayer())
				this.own = ((PlayerUpdated) data).getPlayer();
		}
		if (data instanceof MoveRequest)
		{
			client.sendMessageToRoom(roomId, new Move(MoveTyp.MOVE, board
					.nextFreeFieldFor(own)));
		}
	}

	@Override
	public void onError(ErrorResponse response)
	{
		System.err.println(response);
	}

	@Override
	public void onNewState(String roomId, Object state)
	{
		System.out.println("new state received" + state);
	}

	public void joinAnyGame()
	{
		client.joinAnyGame(GamePlugin.PLUGIN_UUID);
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
	public void onGameOver(String roomId, GameResult data)
	{
		// TODO Auto-generated method stub
		
	}
}
