package sc.plugin2017;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.IPlayer;
import sc.framework.plugins.protocol.MoveRequest;
import sc.networking.clients.IControllableGame;
import sc.networking.clients.ILobbyClientListener;
import sc.networking.clients.LobbyClient;
import sc.plugin2017.util.Configuration;
import sc.protocol.helpers.RequestResult;
import sc.protocol.responses.ErrorResponse;
import sc.protocol.responses.PrepareGameResponse;
import sc.shared.GameResult;
import sc.shared.SlotDescriptor;

/**
 * Abstrakter Client nach Vorschrift des SDKs. Beinhaltet einen LobbyClient, der
 * den tatsächlichen Client darstellt.
 *
 * @author sven, tkra
 */
public abstract class AbstractClient implements ILobbyClientListener {
  private static final Logger logger = LoggerFactory
      .getLogger(AbstractClient.class);
	// The handler reacts to messages from the server received by the lobby
	// client
	protected IGameHandler handler;

	// The lobby client, that connects to the room
	private LobbyClient client;

	private String gameType;

	// If the client made an error (rule violation), store reason here
	private String error;

	// current id to identify the client instance internal
	private EPlayerId id;
	// the current room in which the player is
	private String roomId;
	// the current host
	private String host;
	// the current port
	private int port;
	// current figurecolor to identify which client belongs to which player
	private PlayerColor myColor;
	// set to true when ready was sent to ReadyListeners
	protected boolean alreadyReady = false;

	public AbstractClient(String host, int port, EPlayerId id) throws IOException {
		gameType = GamePlugin.PLUGIN_UUID;
		client = new LobbyClient(Configuration.getXStream(), Configuration.getClassesToRegister(), host, port);
		client.addListener(this);
		client.start();
		this.id = id;
		this.port = port;
		this.host = host;
		error = null;
	}

	// wenn es nur einen client gibt
	public AbstractClient(String host, int port) throws IOException {
		this(host, port, EPlayerId.PLAYER_ONE);
	}

	public void setHandler(IGameHandler handler) {
		this.handler = handler;
	}

	public IGameHandler getHandler() {
		return handler;
	}

	/**
	 * Tell this client to observe the game given by the preparation handler
	 *
	 * @param handle Handle
	 * @return controllable game
	 */
	public IControllableGame observeGame(PrepareGameResponse handle) {
		return client.observe(handle);
	}

	/**
	 * start observation with control over the game (pause etc)
	 *
	 * @param handle
	 *            comes from prepareGame()
	 * @return controllinstance to do pause, unpause etc
	 */
	public IControllableGame observeAndControl(PrepareGameResponse handle) {
		return client.observeAndControl(handle);
	}

	/**
	 * Called when a new message is sent to the room, e.g. move requests
	 */
	@Override
	public void onRoomMessage(String roomId, Object data) {
		if (data instanceof MoveRequest) {
			handler.onRequestAction();
		} else if (data instanceof WelcomeMessage) {
			WelcomeMessage welc = (WelcomeMessage) data;
			myColor = welc.getYourColor();
		}
		this.roomId = roomId;
	}

	/**
	 * sends the <code>move</code> to the server
	 *
	 * @param move
	 *            the move you want to do
	 */
	public void sendMove(Move move) {
		client.sendMessageToRoom(roomId, move);
	}

	/**
	 * Called, when an error is sent to the room
	 */
	@Override
	public void onError(String roomId, ErrorResponse response) {
	  logger.debug("onError: Client {} received error {}", this, response.getMessage());
		this.error = response.getMessage();
	}

	/**
	 * Called when game state has been received Happens, after a client made a
	 * move.
	 */
	@Override
	public void onNewState(String roomId, Object state) {

		GameState gameState = (GameState) state;
	  logger.debug("{} got new state {}", this, gameState);

		if (id != EPlayerId.OBSERVER) {
			handler.onUpdate(gameState);

			if (gameState.getCurrentPlayer().getPlayerColor() == myColor) {
				// active player is own
				handler.onUpdate(gameState.getCurrentPlayer(), gameState.getOtherPlayer());
			} else {
				// active player is the enemy
				handler.onUpdate(gameState.getOtherPlayer(), gameState.getCurrentPlayer());
			}
		}
	}

	public void joinAnyGame() {
		client.joinAnyGame(gameType);
	}

	@Override
	public void onGameJoined(String roomId) {

	}

	@Override
	public void onGameLeft(String roomId) {
	  logger.debug("{} got game left {}", this, roomId);

	}

	public void joinPreparedGame(String reservation) {
		client.joinPreparedGame(reservation);
	}

	/**
	 * @return String gameType
	 */
	public String getGameType() {
		return gameType;
	}

	public EPlayerId getID() {
		return id;
	}

	public void prepareGame(int playerCount) {
		client.prepareGame(gameType, playerCount);
	}

	@Override
	public void onGamePrepared(PrepareGameResponse response) {
		// not needed
	}

	public RequestResult<PrepareGameResponse> prepareGameAndWait(SlotDescriptor... descriptors)
			throws InterruptedException {
		return client.prepareGameAndWait(gameType, descriptors);
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	@Override
	public void onGameOver(String roomId, GameResult data) {
		logger.debug("{} onGameOver got game result {}", this, data);
		if (handler != null) {
			handler.gameEnded(data, myColor, this.error);
		}
	}

	public void freeReservation(String reservation) {
		client.freeReservation(reservation);
	}

	@Override
	public void onGamePaused(String roomId, IPlayer nextPlayer) {
		// not needed
	}

	public String getError() {
		return error;
	}

	public PlayerColor getMyColor() {
		return myColor;
	}

}
