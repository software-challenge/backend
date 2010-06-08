package sc.plugin2011;

import sc.api.plugins.IPlayer;
import sc.networking.clients.ILobbyClientListener;
import sc.networking.clients.LobbyClient;
import sc.plugin2011.EPlayerId;
import sc.plugin2011.PlayerColor;
import sc.plugin2011.IGameHandler;
import sc.protocol.responses.ErrorResponse;
import sc.protocol.responses.PrepareGameResponse;
import sc.shared.GameResult;

public class AbstractClient implements ILobbyClientListener {
	
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
	private PlayerColor		mycolor;
	// set to true when ready was sent to ReadyListeners
	protected boolean		alreadyReady	= false;

	@Override
	public void onError(String roomId, ErrorResponse error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGameJoined(String roomId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGameLeft(String roomId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGameOver(String roomId, GameResult data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGamePaused(String roomId, IPlayer nextPlayer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGamePrepared(PrepareGameResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewState(String roomId, Object state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRoomMessage(String roomId, Object data) {
		// TODO Auto-generated method stub
		
	}
	
}
