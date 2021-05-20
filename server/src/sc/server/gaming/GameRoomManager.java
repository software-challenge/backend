package sc.server.gaming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.api.plugins.IGameState;
import sc.api.plugins.exceptions.GameRoomException;
import sc.api.plugins.exceptions.RescuableClientException;
import sc.api.plugins.host.GameLoader;
import sc.networking.InvalidScoreDefinitionException;
import sc.protocol.requests.PrepareGameRequest;
import sc.protocol.responses.GamePreparedResponse;
import sc.protocol.responses.RoomWasJoinedEvent;
import sc.server.Configuration;
import sc.server.network.Client;
import sc.server.plugins.GamePluginInstance;
import sc.server.plugins.GamePluginManager;
import sc.server.plugins.UnknownGameTypeException;
import sc.shared.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

/**
 * The GameManager is responsible to keep all games alive and kill them once
 * they are done. Additionally the GameManager has to detect and kill games
 * which seem dead-locked or have caused a timeout.
 */
public class GameRoomManager {
  private Map<String, GameRoom> rooms;

  private final GamePluginManager gamePluginManager = new GamePluginManager();

  private static final Logger logger = LoggerFactory.getLogger(GameRoomManager.class);

  private List<Score> scores = new ArrayList<>();

  /** Default constructor, initializes rooms, loads available plugins. */
  public GameRoomManager() {
    this.rooms = new HashMap<>();
    this.gamePluginManager.reload();
    this.gamePluginManager.activateAllPlugins();
  }

  /** Adds an active GameRoom to this <code>GameManager</code> */
  private synchronized void add(GameRoom room) {
    logger.debug("Adding room with id {}", room.getId());
    this.rooms.put(room.getId(), room);
  }

  public IGamePlugin findPlugin(String gameType) throws RescuableClientException {
    GamePluginInstance plugin = this.gamePluginManager.getPlugin(gameType);

    if (plugin == null) {
      logger.warn("Couldn't find a game of type " + gameType);
      throw new UnknownGameTypeException(gameType, this.gamePluginManager.getPluginUUIDs());
    }

    return plugin.getPlugin();
  }

  /**
   * Create a not prepared {@link GameRoom GameRoom} of given type.
   *
   * @return Newly created GameRoom
   *
   * @throws RescuableClientException if creation of game failed
   */
  public synchronized GameRoom createGameRoom(String gameType) throws RescuableClientException {
    IGamePlugin plugin = findPlugin(gameType);
    IGameInstance game;

    String gameFileLocation = Configuration.get(Configuration.GAMELOADFILE);
    if (gameFileLocation != null && !gameFileLocation.equals("")) {
      File gameFile = new File(gameFileLocation);
      int turn = 0;
      try {
        turn = Integer.parseInt(Configuration.get(Configuration.TURN_TO_LOAD));
      } catch(NumberFormatException ignored) {
      }

      // TODO test this
      logger.info("Loading game from file '{}' at turn {}", gameFile, turn);
      try {
        game = plugin.createGameFromState(new GameLoader().loadGame(gameFile, turn));
      } catch(IOException e) {
        logger.error("Failed to load game from file", e);
        game = plugin.createGame();
      }
    } else {
      game = plugin.createGame();
    }

    return createGameRoom(plugin.getScoreDefinition(), game, false);
  }

  /** Create a new GameRoom with the given definitions. */
  public GameRoom createGameRoom(ScoreDefinition scoreDefinition, IGameInstance game, boolean prepared) {
    GameRoom room = new GameRoom(generateRoomId(), this, scoreDefinition, game);
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
  public synchronized RoomWasJoinedEvent createAndJoinGame(Client client, String gameType)
          throws RescuableClientException {
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
  public synchronized RoomWasJoinedEvent joinOrCreateGame(Client client, String gameType)
          throws RescuableClientException {
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

  public GamePluginManager getPluginManager() {
    return this.gamePluginManager;
  }

  /**
   * Creates a new GameRoom with reserved PlayerSlots according to the
   * descriptors and loads a game state from a file if provided.
   *
   * @return new PrepareGameProtocolMessage with roomId and slot reservations
   *
   * @throws RescuableClientException if game could not be created
   */
  public synchronized GamePreparedResponse prepareGame(String gameType, boolean paused, SlotDescriptor[] descriptors, IGameState loadGameInfo)
          throws RescuableClientException {
    IGamePlugin plugin = findPlugin(gameType);
    IGameInstance game = loadGameInfo != null ? plugin.createGameFromState(loadGameInfo) : plugin.createGame();

    GameRoom room = createGameRoom(plugin.getScoreDefinition(), game, true);
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

  public List<Score> getScores() {
    return scores;
  }

  /**
   * Called by gameRoom after game ended and test mode enabled to save results in playerScores.
   *
   * @param name1        displayName of player1
   * @param name2        displayName of player2
   *
   * @throws InvalidScoreDefinitionException if scoreDefinitions do not match
   */
  public void addResultToScore(GameResult result, String name1, String name2) throws InvalidScoreDefinitionException {
    if (name1.equals(name2)) {
      logger.warn("Both player playerScores have the same displayName. Won't save test relevant data");
      return;
    }
    ScoreDefinition scoreDefinition = result.getDefinition();
    Score firstScore = null;
    Score secondScore = null;
    for (Score score : this.scores) {
      if (score.getDisplayName().equals(name1)) {
        firstScore = score;
      } else if (score.getDisplayName().equals(name2)) {
        secondScore = score;
      }
    }
    if (firstScore == null) {
      firstScore = new Score(scoreDefinition, name1);
      this.scores.add(firstScore);
    }
    if (secondScore == null) {
      secondScore = new Score(scoreDefinition, name2);
      this.scores.add(secondScore);
    }

    final List<PlayerScore> playerScores = result.getScores();
    firstScore.setNumberOfTests(firstScore.getNumberOfTests() + 1);
    secondScore.setNumberOfTests(secondScore.getNumberOfTests() + 1);
    for (int i = 0; i < scoreDefinition.getSize(); i++) {
      ScoreFragment fragment = scoreDefinition.get(i);
      ScoreValue firstValue = firstScore.getScoreValues().get(i);
      ScoreValue secondValue = secondScore.getScoreValues().get(i);
      if (!fragment.equals(firstValue.getFragment()) || !fragment.equals(secondValue.getFragment())) {
        logger.error("Could not add current game result to score. Score definition of player and result do not match.");
        throw new InvalidScoreDefinitionException("ScoreDefinition of player does not match expected score definition");
      }
      if (Objects.equals(fragment.getAggregation(), ScoreAggregation.AVERAGE)) {
        firstValue.setValue(updateAverage(firstValue.getValue(), firstScore.getNumberOfTests(), playerScores.get(0).getValues().get(i)));
        secondValue.setValue(updateAverage(secondValue.getValue(), secondScore.getNumberOfTests(), playerScores.get(1).getValues().get(i)));
      } else if (Objects.equals(fragment.getAggregation(), ScoreAggregation.SUM)) {
        firstValue.setValue(firstValue.getValue().add(playerScores.get(0).getValues().get(i)));
        secondValue.setValue(secondValue.getValue().add(playerScores.get(1).getValues().get(i)));
      }
    }
  }

  /** Calculates a new average value: average = oldAverage * ((#amount - 1)/ #amount) + newValue / #amount */
  private BigDecimal updateAverage(BigDecimal oldAverage, int amount, BigDecimal newValue) {
    BigDecimal decAmount = new BigDecimal(amount);
    return oldAverage.multiply(decAmount.subtract(BigDecimal.ONE).divide(decAmount, Configuration.BIG_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP))
            .add(newValue.divide(decAmount, Configuration.BIG_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP))
            .round(new MathContext(Configuration.BIG_DECIMAL_SCALE + 2));
  }

}
