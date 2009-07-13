package sc.plugin2010;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.IPlayer;
import sc.framework.plugins.protocol.MoveRequest;
import sc.plugin2010.Player.FigureColor;
import sc.plugin2010.gui.Observation;
import sc.plugin2010.util.Configuration;
import sc.protocol.ErrorResponse;
import sc.protocol.IControllableGame;
import sc.protocol.ILobbyClientListener;
import sc.protocol.LobbyClient;
import sc.protocol.RequestResult;
import sc.protocol.responses.PrepareGameResponse;
import sc.shared.GameResult;

/**
 * Der Client für das Hase- und Igel Plugin.
 * 
 * @author rra
 * @since Jul 5, 2009
 * 
 */
public class Client implements ILobbyClientListener
{
	private static final Logger	logger			= LoggerFactory
														.getLogger(Client.class);
	private IGameHandler		handler;
	private LobbyClient			client;
	private Observation			obs;
	private String				gameType;
	// current id to identify the client instance internal
	private EPlayerId			id;
	// the current room in which the player is
	private String				roomId;
	private String				host;
	private int					port;
	private FigureColor			mycolor;
	private boolean				alreadyReady	= false;

	@SuppressWarnings("unchecked")
	public Client(String host, int port, EPlayerId id) throws IOException
	{
		gameType = GamePlugin.PLUGIN_UUID;
		client = new LobbyClient(Configuration.getXStream(), Arrays.asList(
				Player.class, Move.class, Board.class, GameState.class), host,
				port);
		client.addListener(this);
		client.start();
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

	public void setObservation(Observation obs)
	{
		this.obs = obs;
		// this.client.start();
	}

	public Observation getObservation()
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

		if (id == EPlayerId.OBSERVER)
		{
			mycolor = FigureColor.RED; // set Red as first Player
		}

		GameState gameState = (GameState) state;
		Game game = gameState.getGame();
		handler.onUpdate(game.getBoard(), game.getTurn());

		if (game.getActivePlayer().getColor() == mycolor)
		{
			handler.onUpdate(game.getBoard().getOtherPlayer( // TODO do it in
					// one procedure
					game.getActivePlayer()), false);
			handler.onUpdate(game.getActivePlayer(), true);
		}
		else
		{
			handler.onUpdate(game.getActivePlayer(), false);
			handler.onUpdate(game.getBoard().getOtherPlayer(
					game.getActivePlayer()), true);

		}

		if (obs != null)
		{
			int playerid = 0;
			if (game.getActivePlayer().getColor() == FigureColor.RED)
			{
				playerid = 0;
			}
			else
			{
				playerid = 1;
			}
			;

			Player oldPlayer = game.getBoard().getOtherPlayer(
					game.getActivePlayer());
			// TODO sent 2 moves if 2 moves were made
			obs.newTurn(playerid, GameUtil.displayMoveAction(oldPlayer
					.getLastMove()));

			if (!alreadyReady)
			{
				alreadyReady = true;
				obs.ready();
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
	public void onGameJoined(String roomId)
	{
		if (obs != null)
		{
			obs.ready();
		}
	}

	@Override
	public void onGameLeft(String roomId)
	{
		if (obs != null)
		{
			obs.gameEnded(null);
		}
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

	@Override
	public void onGameOver(String roomId, GameResult data)
	{
		handler.gameEnded(data);

		if (obs != null)
		{
			obs.gameEnded(data);
		}

		try
		{
			client.close();
		}
		catch (IOException e)
		{
			// not needed
		}
	}

	public void freeReservation(String reservation)
	{
		client.freeReservation(reservation);
	}

	@Override
	public void onGamePaused(String roomId, IPlayer nextPlayer)
	{
		// TODO Auto-generated method stub
	}
}
