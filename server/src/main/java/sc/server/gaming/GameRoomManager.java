package sc.server.gaming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.api.plugins.IGameState;
import sc.api.plugins.exceptions.GameRoomException;
import sc.api.plugins.exceptions.RescuableClientException;
import sc.framework.ReplayLoader;
import sc.protocol.requests.PrepareGameRequest;
import sc.protocol.responses.GamePreparedResponse;
import sc.protocol.responses.RoomWasJoinedEvent;
import sc.server.Configuration;
import sc.server.network.Client;
import sc.shared.SlotDescriptor;

import java.io.File;
import java.util.*;

/**
 * The GameManager is responsible to keep all games alive and kill them once
 * they are done. Additionally the GameManager has to detect and kill games
 * which seem dead-locked or have caused a timeout.
 */
public class GameRoomManager {
  private Map<String, GameRoom> rooms;

  private static final Logger logger = LoggerFactory.getLogger(GameRoomManager.class);

  /** Default constructor, initializes rooms, loads available plugins. */
  public GameRoomManager() {
    this.rooms = new HashMap<>();
  }

  /** Adds an active GameRoom to this <code>GameManager</code> */
  private synchronized void add(GameRoom room) {
    logger.debug("Adding room with id {}", room.getId());
    this.rooms.put(room.getId(), room);
  }

  /**
   * Create a not prepared {@link GameRoom GameRoom} of given type.
   *
   * @return Newly created GameRoom
   *
   * @throws RescuableClientException if creation of game failed
   */
  public synchronized GameRoom createGameRoom(String gameType) {
    IGamePlugin plugin = IGamePlugin.loadPlugin(gameType);
    IGameInstance game;

    String gameFileLocation = Configuration.get(Configuration.GAMELOADFILE);
    if (gameFileLocation != null && !gameFileLocation.equals("")) {
      File gameFile = new File(gameFileLocation);
      int turn = 0;
      try {
        turn = Integer.parseInt(Configuration.get(Configuration.TURN_TO_LOAD));
      } catch(NumberFormatException ignored) {
      }

      logger.info("Loading game from file '{}' at turn {}", gameFile, turn);
      game = plugin.createGameFromState(new ReplayLoader(gameFile).getTurn(turn));
    } else {
      game = plugin.createGame();
    }

    return createGameRoom(game, false);
  }

  /** Create a new GameRoom with the given definitions. */
  public GameRoom createGameRoom(IGameInstance game, boolean prepared) {
    GameRoom room = new GameRoom(generateRoomId(), this, game);
    // pause room on JoinRoomRequest if specified in server.properties
    if (!prepared) {
      boolean paused = Boolean.parseBoolean(Configuration.get(Configuration.PAUSED));
      room.pause(paused);
    }

    this.add(room);

    return room;
  }

  private static synchronized String generateRoomId() {
    return UUID.randomUUID().toString();
  }

  /**
   * Open new GameRoom and join the client.
   *
   * @return GameRoomMessage with roomId, null if unsuccessful
   *
   * @throws RescuableClientException if game could not be created
   */
  public synchronized RoomWasJoinedEvent createAndJoinGame(Client client, String gameType) {
    GameRoom room = createGameRoom(gameType);
    if (room.join(client)) {
      return roomJoined(room);
    }
    return null;
  }

  /**
   * Called on JoinRoomRequest. Client joins an already existing open GameRoom or opens new one and joins.
   *
   * @return GameRoomMessage with roomId, null if unsuccessful
   *
   * @throws RescuableClientException if client could not join room
   */
  public synchronized RoomWasJoinedEvent joinOrCreateGame(Client client, String gameType) throws RescuableClientException {
    for (GameRoom gameRoom : getGames()) {
      // TODO gameType isn't checked
      if (gameRoom.join(client)) {
        return roomJoined(gameRoom);
      }
    }
    return createAndJoinGame(client, gameType);
  }

  protected RoomWasJoinedEvent roomJoined(GameRoom room) {
    return new RoomWasJoinedEvent(room.getId(), room.getClients().size());
  }

  /** Create an unmodifiable Collection of the {@link GameRoom GameRooms}. */
  public synchronized Collection<GameRoom> getGames() {
    return Collections.unmodifiableCollection(this.rooms.values());
  }

  /**
   * Creates a new GameRoom with reserved PlayerSlots according to the
   * descriptors and loads a game state from a file if provided.
   *
   * @return new PrepareGameProtocolMessage with roomId and slot reservations
   *
   * @throws RescuableClientException if game could not be created
   */
  public synchronized GamePreparedResponse prepareGame(String gameType, boolean paused, SlotDescriptor[] descriptors, IGameState loadGameInfo) {
    IGamePlugin plugin = IGamePlugin.loadPlugin(gameType);
    IGameInstance game = loadGameInfo != null ? plugin.createGameFromState(loadGameInfo) : plugin.createGame();

    GameRoom room = createGameRoom(game, true);
    room.pause(paused);

    return new GamePreparedResponse(room.getId(), room.reserveSlots(descriptors));
  }

  /**
   * Overload for {@link #prepareGame}.
   *
   * @return new PrepareGameProtocolMessage with roomId and slot reservations
   *
   * @throws RescuableClientException if game could not be created
   */
  public GamePreparedResponse prepareGame(PrepareGameRequest prepared) throws RescuableClientException {
    return prepareGame(
            prepared.getGameType(),
            prepared.getPause(),
            prepared.getSlotDescriptors(),
            null
    );
  }

  /**
   * @param roomId String Id of room to be found
   *
   * @return returns GameRoom specified by roomId
   *
   * @throws RescuableClientException if no room could be found
   */
  public synchronized GameRoom findRoom(String roomId) throws RescuableClientException {
    GameRoom room = this.rooms.get(roomId);

    if (room == null) {
      throw new GameRoomException("Couldn't find a room with id " + roomId);
    }

    return room;
  }

  /** Remove specified room from this manager. */
  public synchronized void remove(GameRoom gameRoom) {
    this.rooms.remove(gameRoom.getId());
  }

}
