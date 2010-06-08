package sc.plugin_minimal;

import java.io.IOException;
import java.util.Map;

import sc.api.plugins.IPlayer;
import sc.framework.plugins.protocol.MoveRequest;
import sc.networking.clients.IControllableGame;
import sc.networking.clients.ILobbyClientListener;
import sc.networking.clients.LobbyClient;
import sc.plugin_minimal.util.Configuration;
import sc.protocol.helpers.RequestResult;
import sc.protocol.responses.ErrorResponse;
import sc.protocol.responses.PrepareGameResponse;
import sc.shared.GameResult;
import sc.shared.SlotDescriptor;

public abstract class AbstractClient implements ILobbyClientListener
{
	protected IGameHandler	handler;
	private LobbyClient		client;
	private String			gameType;
	private String			error;

	// current id to identify the client instance internal
	private EPlayerId		id;
	// the current room in which the player is
	private String			roomId;
	// the current host
	private String			host;
	// the current port
	private int				port;
	// current figurecolor to identify which client belongs to which player
	private FigureColor		mycolor;
	// set to true when ready was sent to ReadyListeners
	protected boolean		alreadyReady	= false;

	public AbstractClient(String host, int port, EPlayerId id)
			throws IOException
	{
		gameType = GamePlugin.PLUGIN_UUID;
		client = new LobbyClient(Configuration.getXStream(), Configuration
				.getClassesToRegister(), host, port);
		client.addListener(this);
		client.start();
		this.id = id;
		this.port = port;
		this.host = host;
		error = null;
	}

	public void setHandler(IGameHandler handler)
	{
		this.handler = handler;
	}

	public IGameHandler getHandler()
	{
		return handler;
	}

	public IControllableGame observeGame(PrepareGameResponse handle)
	{
		return client.observe(handle);
	}

	/**
	 * start observation with control over the game (pause etc)
	 * 
	 * @param handle
	 *            comes from prepareGame()
	 * @return controllinstance to do pause, unpause etc
	 */
	public IControllableGame observeAndControl(PrepareGameResponse handle)
	{
		return client.observeAndControl(handle);
	}

	@Override
	public void onRoomMessage(String roomId, Object data)
	{
		if (data instanceof MoveRequest)
		{
			handler.onRequestAction();
		}
		else if (data instanceof WelcomeMessage)
		{
			WelcomeMessage welc = (WelcomeMessage) data;
			mycolor = welc.getYourColor();
		}
		this.roomId = roomId;
	}

	/**
	 * sends the <code>move</code> to the server
	 * 
	 * @param move
	 *            the move you want to do
	 */
	public void sendMove(Move move)
	{
		client.sendMessageToRoom(roomId, move);
	}

	@Override
	public void onError(String roomId, ErrorResponse response)
	{
		System.err.println(response.getMessage());
		this.error = response.getMessage();
	}

	@Override
	public void onNewState(String roomId, Object state)
	{
		GameState gameState = (GameState) state;
		Game game = gameState.getGame();

		if (id != EPlayerId.OBSERVER)
		{
			handler.onUpdate(game.getBoard(), game.getTurn());

			if (game.getActivePlayer().getColor() == mycolor)
			{ // active player is own
				handler.onUpdate(game.getActivePlayer(), game.getBoard()
						.getOtherPlayer(game.getActivePlayer()));
			}
			else
			// active player is the enemy
			{
				handler.onUpdate(game.getBoard().getOtherPlayer(
						game.getActivePlayer()), game.getActivePlayer());

			}
		}
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
	public void onGamePrepared(PrepareGameResponse response)
	{
		// not needed
	}

	public RequestResult<PrepareGameResponse> prepareGameAndWait(
			SlotDescriptor... descriptors) throws InterruptedException
	{
		return client.prepareGameAndWait(gameType, descriptors);
	}

	public String getHost()
	{
		return host;
	}

	public int getPort()
	{
		return port;
	}

	@Override
	public void onGameOver(String roomId, GameResult data)
	{
		client.close();

		if (handler != null)
		{
			handler.gameEnded(data, mycolor, this.error);
		}
	}

	public void freeReservation(String reservation)
	{
		client.freeReservation(reservation);
	}

	@Override
	public void onGamePaused(String roomId, IPlayer nextPlayer)
	{
		// not needed
	}
	
	public String getError() {
		return error;
	}
	
}
