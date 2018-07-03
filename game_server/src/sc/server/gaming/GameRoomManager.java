package sc.server.gaming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.exceptions.RescuableClientException;
import sc.networking.InvalidScoreDefinitionException;
import sc.protocol.requests.PrepareGameRequest;
import sc.protocol.responses.GameRoomMessage;
import sc.protocol.responses.PrepareGameProtocolMessage;
import sc.server.Configuration;
import sc.server.network.Client;
import sc.server.plugins.GamePluginInstance;
import sc.server.plugins.GamePluginManager;
import sc.server.plugins.UnknownGameTypeException;
import sc.shared.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

/**
 * The GameManager is responsible to keep all games alive and kill them once
 * they are done. Additionally the GameManger has to detect and kill games,
 * which seem to be dead-locked or have caused a timeout.
 */
public class GameRoomManager {
  /* Private fields  */
  private Map<String, GameRoom> rooms;
  private GamePluginApi pluginApi;
  
  private final GamePluginManager gamePluginManager = new GamePluginManager();
  
  private static final Logger logger = LoggerFactory.getLogger(GameRoomManager.class);
  
  private List<Score> scores = new ArrayList<>();
  
  /** Default constructor, initializes rooms, loads available plugins */
  public GameRoomManager() {
    this.rooms = new HashMap<>();
    this.pluginApi = new GamePluginApi();
    this.gamePluginManager.reload();
    this.gamePluginManager.activateAllPlugins(this.pluginApi);
  }
  
  /**
   * Adds an active game to the <code>GameManager</code>
   *
   * @param room Room to be added
   */
  private void add(GameRoom room) {
    logger.debug("Adding room with id {}", room.getId());
    this.rooms.put(room.getId(), room);
  }
  
  /**
   * Create a not prepared {@link GameRoom GameRoom} of given type
   *
   * @param gameType String of current Game
   *
   * @return Newly created GameRoom
   *
   * @throws RescuableClientException if creation of game failed
   */
  public synchronized GameRoom createGame(String gameType) throws RescuableClientException {
    return createGame(gameType, false);
  }
  
  /**
   * make new PluginManager, generate roomId, create Game and GameRoom. If gameFile is set, load gameState from file
   *
   * @param gameType String of current Game
   * @param prepared signals whether the game was prepared by gui or ..., false if player has to send JoinRoomRequest
   *
   * @return newly created GameRoom
   *
   * @throws RescuableClientException if Plugin could not be loaded
   */
  public synchronized GameRoom createGame(String gameType, boolean prepared) throws RescuableClientException {
    GamePluginInstance plugin = this.gamePluginManager.getPlugin(gameType);
    
    if (plugin == null) {
      logger.warn("Couldn't find a game of type " + gameType);
      throw new UnknownGameTypeException(gameType, this.gamePluginManager.getPluginUUIDs());
    }
    
    logger.info("Created new game of type " + gameType);
    
    String roomId = generateRoomId();
    GameRoom room = new GameRoom(roomId, this, plugin, plugin.createGame(), prepared);
    // pause room if specified in server.properties on joinRoomRequest
    if (!prepared) {
      boolean paused = Boolean.parseBoolean(Configuration.get(Configuration.PAUSED));
      room.pause(paused);
      logger.info("Pause is set to {}", paused);
    }
    
    String gameFile = Configuration.get(Configuration.GAMELOADFILE);
    if (gameFile != null && !gameFile.equals("")) {
      logger.info("Request plugin to load game from file: " + gameFile);
      int turn;
      if (Configuration.get(Configuration.TURN_TO_LOAD) != null) {
        turn = Integer.parseInt(Configuration.get(Configuration.TURN_TO_LOAD));
      } else {
        turn = 0;
      }
      logger.debug("Turns is to load is: " + turn);
      if (turn > 0) {
        logger.debug("Loading from non default turn");
        room.getGame().loadFromFile(gameFile, turn);
      } else {
        logger.debug("Loading first gameState found");
        room.getGame().loadFromFile(gameFile);
      }
    }
    
    this.add(room);
    
    return room;
  }
  
  private static synchronized String generateRoomId() {
    return UUID.randomUUID().toString();
  }
  
  /**
   * Open new GameRoom and join Client
   *
   * @param client   Client to join the game
   * @param gameType String of current game
   *
   * @return GameRoomMessage for new GameRoom, null im unsuccessful
   *
   * @throws RescuableClientException if game could not be created
   */
  public synchronized GameRoomMessage createAndJoinGame(Client client, String gameType)
      throws RescuableClientException {
    GameRoom room = createGame(gameType);
    if (room.join(client)) {
      return new GameRoomMessage(room.getId(), false);
    }
    return null;
  }
  
  /**
   * Called after JoinRoomRequest. Client joins already existing GameRoom or opens new one
   *
   * @param client   to join the game
   * @param gameType String of current game
   *
   * @return GameRoomMessage with roomId an success null if unsuccessful
   *
   * @throws RescuableClientException if client could not join room
   */
  public synchronized GameRoomMessage joinOrCreateGame(Client client, String gameType)
      throws RescuableClientException {
    for (GameRoom gameRoom : getGames()) {
      if (gameRoom.join(client)) {
        return new GameRoomMessage(gameRoom.getId(), true);
      }
    }
    
    return createAndJoinGame(client, gameType);
  }
  
  /**
   * Create Collection of {@link GameRoom GameRooms}, which can not be modified
   *
   * @return Collection<GameRoom>
   */
  public synchronized Collection<GameRoom> getGames() {
    return Collections.unmodifiableCollection(this.rooms.values());
  }
  
  /**
   * Getter for {@link sc.server.plugins.PluginManager PluginManager}
   *
   * @return PluginManager
   */
  public GamePluginManager getPluginManager() {
    return this.gamePluginManager;
  }
  
  /**
   * Getter for {@link GamePluginApi GamePluginApi}
   *
   * @return GamePluginApi
   */
  public GamePluginApi getPluginApi() {
    return this.pluginApi;
  }
  
  /**
   * Creates a new GameRoom {@link #createGame(String) createGame}, set descriptors of PlayerSlots,
   * if exists load state of game from file
   *
   * @param gameType     String of current game
   * @param descriptors  which are displayName, canTimeout and shouldBePaused
   * @param loadGameInfo Object for game information
   *
   * @return new PrepareGameProtocolMessage with roomId and slots
   *
   * @throws RescuableClientException if game could not be created
   */
  public synchronized PrepareGameProtocolMessage prepareGame(String gameType, List<SlotDescriptor> descriptors, Object loadGameInfo)
      throws RescuableClientException {
    GameRoom room = createGame(gameType, true);
    room.openSlots(descriptors);
    
    if (loadGameInfo != null) {
      room.getGame().loadGameInfo(loadGameInfo);
    }
    
    return new PrepareGameProtocolMessage(room.getId(), room.reserveAllSlots());
  }
  
  /**
   * Calls {@link #prepareGame(String, List, Object) prepareGame}
   *
   * @param prepared PrepareGameRequest with gameType and slotsDescriptors
   *
   * @return ProtocolMessage from server
   *
   * @throws RescuableClientException if room could not be created
   */
  public synchronized PrepareGameProtocolMessage prepareGame(PrepareGameRequest prepared) throws RescuableClientException {
    return prepareGame(
        prepared.getGameType(),
        prepared.getSlotDescriptors(), prepared.getLoadGameInfo());
  }
  
  /**
   * Getter for GameRoom
   *
   * @param roomId String Id of room to be found
   *
   * @return returns GameRoom specified by rooId
   *
   * @throws RescuableClientException if no room could be found
   */
  public synchronized GameRoom findRoom(String roomId)
      throws RescuableClientException {
    GameRoom room = this.rooms.get(roomId);
    
    if (room == null) {
      throw new RescuableClientException("Couldn't find a room with id " + roomId);
    }
    
    return room;
  }
  
  /**
   * Remove specified room from game
   *
   * @param gameRoom to be removed
   */
  public void remove(GameRoom gameRoom) {
    this.rooms.remove(gameRoom.getId());
  }
  
  public List<Score> getScores() {
    return scores;
  }
  
  /**
   * Called by gameRoom after game ended and test mode enabled to save results in playerScores
   *
   * @param result       GameResult
   * @param playerScores List of playerScores
   * @param name1        displayName of player1
   * @param name2        displayName of player2
   *
   * @throws InvalidScoreDefinitionException if scoreDefinitions do not match
   */
  public void addResultToScore(GameResult result, List<PlayerScore> playerScores, String name1, String name2) throws InvalidScoreDefinitionException {
    if (name1.equals(name2)) {
      logger.warn("Both player playerScores have the same displayName. Won#t save test relevant data");
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
    
    firstScore.setNumberOfTests(firstScore.getNumberOfTests() + 1);
    secondScore.setNumberOfTests(secondScore.getNumberOfTests() + 1);
    for (int i = 0; i < scoreDefinition.size(); i++) {
      ScoreFragment fragment = scoreDefinition.get(i);
      ScoreValue firstValue = firstScore.getScoreValues().get(i);
      ScoreValue secondValue = secondScore.getScoreValues().get(i);
      if (!fragment.equals(firstValue.getFragment()) || !fragment.equals(secondValue.getFragment())) {
        logger.error("Could not add current game result to score. Score definition of player and result do not match.");
        throw new InvalidScoreDefinitionException("ScoreDefinition of player does not match expected score definition");
      }
      // average = oldaverage * ((#tests - 1)/ #tests) + newValue / #tests
      if (fragment.getAggregation().equals(ScoreAggregation.AVERAGE)) {
        firstValue.setValue((firstValue.getValue().
            multiply(
                (new BigDecimal(firstScore.getNumberOfTests() - 1)
                    .divide(new BigDecimal(firstScore.getNumberOfTests()), Configuration.BIG_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP))
            )).add(
            playerScores.get(0).getValues().get(i).divide(new BigDecimal(firstScore.getNumberOfTests()), Configuration.BIG_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP)));
        secondValue.setValue((secondValue.getValue().
            multiply(
                (new BigDecimal(secondScore.getNumberOfTests() - 1)
                    .divide(new BigDecimal(secondScore.getNumberOfTests()), Configuration.BIG_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP))
            )).add(
            playerScores.get(1).getValues().get(i).divide(new BigDecimal(secondScore.getNumberOfTests()), Configuration.BIG_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP)));
        firstValue.setValue(firstValue.getValue().round(new MathContext(Configuration.BIG_DECIMAL_SCALE + 2)));
        secondValue.setValue(secondValue.getValue().round(new MathContext(Configuration.BIG_DECIMAL_SCALE + 2)));
      } else if (fragment.getAggregation().equals(ScoreAggregation.SUM)) {
        firstValue.setValue(firstValue.getValue().add(playerScores.get(0).getValues().get(i)));
        secondValue.setValue(secondValue.getValue().add(playerScores.get(1).getValues().get(i)));
      }
    }
  }
  
}
