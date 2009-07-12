package sc.plugin2010;

import java.io.IOException;
import java.util.Arrays;

import sc.framework.plugins.protocol.MoveRequest;
import sc.plugin2010.gui.GameObservation;
import sc.protocol.ErrorResponse;
import sc.protocol.IControllableGame;
import sc.protocol.ILobbyClientListener;
import sc.protocol.LobbyClient;
import sc.protocol.RequestResult;
import sc.protocol.clients.ControllingClient;
import sc.protocol.clients.ObservingClient;
import sc.protocol.responses.PrepareGameResponse;

/**
 * Der Client für das Hase- und Igel Plugin.
 * 
 * @author rra
 * @since Jul 5, 2009
 * 
 */
public class Client implements ILobbyClientListener
{
	private IGameHandler	handler;
	private LobbyClient		client;
	private GameObservation	obs;
	private String			gameType;
	// current id to identifiy the client instance internal
	private EPlayerId		id;
	// the current room in which the player is
	private String			roomId;
	private String			host;
	private int				port;

	@SuppressWarnings("unchecked")
	public Client(String host, int port, EPlayerId id) throws IOException
	{
		gameType = GamePlugin.PLUGIN_UUID;
		client = new LobbyClient(host, port, Arrays.asList(Player.class,
				PlayerUpdated.class, Move.class, Board.class,
				BoardUpdated.class));
		client.addListener(this);
		this.id = id;
		this.port = port;
		this.host = host;
	}

	public void setHandler(IGameHandler handler)
	{
		this.handler = handler;
	}

	public IGameHandler getHandler()
	{
		return handler;
	}

	public void setObservation(GameObservation obs)
	{
		this.obs = obs;
	}

	public GameObservation getObservation()
	{
		return obs;
	}

	public IControllableGame observeGame(PrepareGameResponse handle)
	{
		return client.observe(handle);
	}

	public IControllableGame observeAndControl(PrepareGameResponse handle)
	{
		return client.observeAndControl(handle);
	}

	@Override
	public void onRoomMessage(String roomId, Object data)
	{
		if (data instanceof BoardUpdated)
		{
			Board board = ((BoardUpdated) data).getBoard();
			int round = ((BoardUpdated) data).getRound();
			handler.onUpdate(board, round);
		}
		else if (data instanceof PlayerUpdated)
		{
			Player player = ((PlayerUpdated) data).getPlayer();
			boolean own = ((PlayerUpdated) data).isOwnPlayer();
			handler.onUpdate(player, own);
			obs.newTurn("new turn" + (player.getCarrotsAvailable()));
		}
		else if (data instanceof MoveRequest)
		{
			handler.onRequestAction();
		}

		this.roomId = roomId;
	}

	public void sendMove(Move move)
	{
		client.sendMessageToRoom(roomId, move);
	}

	@Override
	public void onError(ErrorResponse response)
	{
		System.err.println(response.getMessage());
	}

	@Override
	public void onNewState(String roomId, Object state)
	{
		/*
		 * GameState gameState = (GameState) state; gameState.getGame().g
		 * 
		 * handler.onUpdate((BoardUpdated) data);
		 * handler.onUpdate((PlayerUpdated) data); obs.newTurn("new turn" +
		 * ((PlayerUpdated) data).getPlayer().getCarrotsAvailable());
		 * handler.onRequestAction();
		 * 
		 * this.roomId = roomId;
		 */// TODO
	}

	public void joinAnyGame()
	{
		client.joinAnyGame(gameType);
	}

	public void joinPreparedGame(String reservation)
	{
		client.joinPreparedGame(reservation);
	}

	/**
	 * @return
	 */
	public String getGameType()
	{
		return gameType;
	}

	public EPlayerId getID()
	{
		return id;
	}

	public void prepareGame(int playerCount)
	{
		client.prepareGame(gameType, playerCount);
	}

	@Override
	public void onGameJoined(String roomId)
	{
		obs.ready();
	}

	@Override
	public void onGameLeft(String roomId)
	{
		obs.gameEnded();
	}

	@Override
	public void onGamePrepared(PrepareGameResponse response)
	{
		// not needed
	}

	public RequestResult<PrepareGameResponse> prepareGameAndWait(int playerCount)
			throws InterruptedException
	{
		return client.prepareGameAndWait(gameType, playerCount);
	}

	public String getHost()
	{
		return host;
	}

	public int getPort()
	{
		return port;
	}
}
