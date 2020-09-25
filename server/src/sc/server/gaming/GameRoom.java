package sc.server.gaming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGameState;
import sc.api.plugins.exceptions.GameLogicException;
import sc.api.plugins.exceptions.RescuableClientException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.api.plugins.host.IGameListener;
import sc.framework.plugins.Player;
import sc.framework.plugins.RoundBasedGameInstance;
import sc.helpers.HelperMethods;
import sc.networking.InvalidScoreDefinitionException;
import sc.networking.clients.IControllableGame;
import sc.networking.clients.LobbyClient;
import sc.networking.clients.ObservingClient;
import sc.networking.clients.XStreamClient;
import sc.protocol.responses.*;
import sc.server.Configuration;
import sc.server.network.Client;
import sc.server.network.DummyClient;
import sc.server.network.IClient;
import sc.shared.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * A wrapper for an actual <code>GameInstance</code>. GameInstances are provided
 * by the plugins. Additional mapping data (Client2Player) will be stored here.
 */
public class GameRoom implements IGameListener {
  private static final Logger logger = LoggerFactory.getLogger(GameRoom.class);

  private final String id;
  private final GameRoomManager gameRoomManager;
  private final ScoreDefinition scoreDefinition;
  private final IGameInstance game;
  private final List<ObserverRole> observers = new ArrayList<>();
  private final List<PlayerSlot> playerSlots = new ArrayList<>(getMaximumPlayerCount());
  private final boolean prepared;
  private GameStatus status = GameStatus.CREATED;
  private GameResult result = null;
  private boolean pauseRequested = false;
  private ObservingClient replayObserver;

  /** currently unused */
  private IControllableGame replay;

  public List<ObserverRole> getObservers() {
    return observers;
  }

  public enum GameStatus {
    CREATED, ACTIVE, OVER
  }

  public GameRoom(String id, GameRoomManager gameRoomManager, ScoreDefinition scoreDefinition, IGameInstance game, boolean prepared) {
    this.id = id;
    // TODO the gameroom shouldn't need to know its manager
    this.gameRoomManager = gameRoomManager;
    this.scoreDefinition = scoreDefinition;
    this.game = game;
    this.prepared = prepared;
    game.addGameListener(this);

    if (Boolean.parseBoolean(Configuration.get(Configuration.SAVE_REPLAY))) {
      try {
        logger.debug("Save replay is active and try to save it to file");
        LobbyClient lobbyClient = new LobbyClient(Configuration.getXStream(), null,
                "127.0.0.1", Configuration.getPort());
        lobbyClient.start();
        lobbyClient.authenticate(Configuration.getAdministrativePassword());
        replayObserver = lobbyClient.observe(this.getId());
      } catch (IOException e) {
        logger.warn("Failed to start replay recording");
        e.printStackTrace();
      }
    }
  }

  public IGameInstance getGame() {
    return this.game;
  }

  /**
   * Generate Game Result, set status to OVER and remove from gameRoomManager
   *
   * @param results result of game
   */
  @Override
  public synchronized void onGameOver(Map<Player, PlayerScore> results) throws InvalidScoreDefinitionException {
    if (isOver()) {
      logger.warn("Game was already over but received another GameOver-Event.");
      return;
    }

    setStatus(GameStatus.OVER);
    this.result = generateGameResult(results);
    logger.info("The game {} is over. (regular={})", getId(), this.result.isRegular());
    broadcast(this.result);

    if (Boolean.parseBoolean(Configuration.get(Configuration.SAVE_REPLAY))) {
      saveReplay();
    }

    // save playerScore if test mode enabled
    if (Boolean.parseBoolean(Configuration.get(Configuration.TEST_MODE))) {
      List<Player> players = game.getPlayers();
      gameRoomManager.addResultToScore(this.getResult(), players.get(0).getDisplayName(), players.get(1).getDisplayName());
    }

    kickAllClients();
    cancel();
  }


  /**
   * Set ScoreDefinition and create GameResult Object from results parameter
   *
   * @param results map of Player and PlayerScore
   *
   * @return GameResult containing all PlayerScores in order and winners
   */
  private GameResult generateGameResult(Map<Player, PlayerScore> results) {
    List<PlayerScore> scores = new ArrayList<>();

    // restore order
    for (PlayerRole player : getPlayers()) {
      PlayerScore score = results.get(player.getPlayer());

      if (score == null)
        throw new RuntimeException("GameScore was not complete!");

      // FIXME: remove cause != unknown
      if (score.getCause() != ScoreCause.UNKNOWN && !score.matches(scoreDefinition))
        throw new RuntimeException(String.format("Score %1s did not match Definition %2s", score, scoreDefinition));

      scores.add(score);
    }
    return new GameResult(scoreDefinition, scores, this.game.getWinners());
  }

  private void saveReplay() {
    List<SlotDescriptor> slotDescriptors = new ArrayList<>();
    for (PlayerSlot slot : this.getSlots()) {
      slotDescriptors.add(slot.getDescriptor());
    }
    String fileName = HelperMethods.generateReplayFilename(this.getGame().getPluginUUID(), slotDescriptors);
    try {
      File f = new File(fileName);
      f.getParentFile().mkdirs();
      f.createNewFile();

      List<ProtocolMessage> replayHistory = replayObserver.getHistory();
      BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
      writer.write("<protocol>\n");
      for (ProtocolMessage element : replayHistory) {
        if (!(element instanceof IGameState))
          continue;
        IGameState state = (IGameState) element;
        MementoPacket data = new MementoPacket(state, null);
        RoomPacket roomPacket = new RoomPacket(this.getId(), data);
        String xmlReplay = Configuration.getXStream().toXML(roomPacket);
        writer.write(xmlReplay + "\n");
        writer.flush();
      }

      String result = Configuration.getXStream().toXML(new RoomPacket(this.getId(), replayObserver.getResult()));
      writer.write(result + "\n");
      writer.write("</protocol>");
      writer.flush();
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Send Object o to all Player in this room
   *
   * @param o Object containing the message
   */
  private void broadcast(ProtocolMessage o) {
    broadcast(o, true);
  }

  /**
   * Send ProtocolMessage to all Players or all Players in this room.
   *
   * @param message      a ProtocolMessage
   * @param roomSpecific only send this room
   */
  private void broadcast(ProtocolMessage message, boolean roomSpecific) {
    ProtocolMessage toSend = message;

    // If message is specific to room, wrap the message in a RoomPacket
    if (roomSpecific) {
      toSend = createRoomPacket(toSend);
    }

    // Send to all Players
    for (PlayerRole player : getPlayers()) {
      logger.debug("Sending {} to {}", message, player);
      player.getClient().send(toSend);
    }

    // Send to all Observer
    observerBroadcast(toSend);
  }

  /** Send Message to all registered Observers. */
  private void observerBroadcast(ProtocolMessage toSend) {
    for (ObserverRole observer : Collections.unmodifiableCollection(this.observers)) {
      logger.debug("Sending {} to observer {}", toSend, observer.getClient().getClass().getSimpleName());
      observer.getClient().send(toSend);
    }
  }

  /** Send {@link GameRoom#broadcast(ProtocolMessage, boolean) broadcast} message with {@link LeftGameEvent LeftGameEvent} */
  private void kickAllClients() {
    logger.debug("Kicking clients and observers");
    broadcast(new LeftGameEvent(getId()), false);
  }

  /** Send updated GameState to all players and observers. */
  @Override
  public void onStateChanged(IGameState data, boolean observersOnly) {
    sendStateToObservers(data);
    if (!observersOnly)
      sendStateToPlayers(data);
  }


  /**
   * {@link GameRoom#broadcast(ProtocolMessage, boolean) Broadcast} the error package to this room
   *
   * @param errorPacket ProtocolErrorMessage
   */
  public void onClientError(ProtocolErrorMessage errorPacket) {
    broadcast(errorPacket, true);
  }

  /** Sends the given IGameState to all Players */
  private void sendStateToPlayers(IGameState data) {
    for (PlayerRole player : getPlayers()) {
      RoomPacket packet = createRoomPacket(new MementoPacket(data, player.getPlayer()));
      player.getClient().send(packet);
    }
  }


  /** Sends the given IGameState to all Observers */
  private void sendStateToObservers(IGameState data) {
    RoomPacket packet = createRoomPacket(new MementoPacket(data, null));

    for (ObserverRole observer : this.observers) {
      logger.debug("sending state to observer {}", observer.getClient());
      observer.getClient().send(packet);
    }
  }

  /**
   * Create {@link RoomPacket RoomPacket} from id and data Object.
   *
   * @param data to be send
   *
   * @return prepared RoomPacket
   */
  public RoomPacket createRoomPacket(ProtocolMessage data) {
    return new RoomPacket(getId(), data);
  }

  /**
   * Getter for Room ID
   *
   * @return id of Room as String
   */
  public String getId() {
    return this.id;
  }

  /**
   * gameRoom
   * Let a client join a GameRoom. Starts a game, if all players joined.
   *
   * @param client Client to join
   *
   * @return true if join successful
   *
   * @throws RescuableClientException
   */
  public synchronized boolean join(Client client)
          throws RescuableClientException {
    PlayerSlot openSlot = null;

    for (PlayerSlot slot : this.playerSlots) {
      // find PlayerSlot that it not in use for the new Client
      if (slot.isEmpty() && !slot.isReserved()) {
        openSlot = slot;
        break;
      }
    }

    // set GameRoom of new Slot, if at least one slot is open
    if (this.playerSlots.size() < getMaximumPlayerCount()) {
      openSlot = new PlayerSlot(this);
      this.playerSlots.add(openSlot);
    }

    if (openSlot == null) {
      return false;
    }

    fillSlot(openSlot, client);

    return true;
  }

  /**
   * If game is not prepared set attributes of PlayerSlot and start game if game is {@link #isReady() ready}
   *
   * @param openSlot PLayerSlot to fill
   * @param client   Client to fill PlayerSlot
   *
   * @throws RescuableClientException
   */
  synchronized void fillSlot(PlayerSlot openSlot, Client client)
          throws RescuableClientException {

    openSlot.setClient(client); // set role of Slot as PlayerRole

    if (!isPrepared()) // is set when game is game is created or prepared
    {
      logger.debug("GameRoom was not prepared, syncSlots");
      // seems to happen every time a client manually connects to server (JoinRoomRequest)
      syncSlot(openSlot);
    }

    startIfReady();
  }

  /**
   * sets player in GameState and sets player specific values (displayName, shoudbePaused, canTimeout).
   * Registers player to role in given slot
   * sends JoinGameProtocolMessage when successful
   *
   * @param slot PlayerSlot to sync
   *
   * @throws RescuableClientException
   */
  private void syncSlot(PlayerSlot slot) throws RescuableClientException {
    // create new player in gameState of game
    Player player = getGame().onPlayerJoined();
    // set attributes for player
    // TODO check whether this is needed for prepared games
    player.setDisplayName(slot.getDescriptor().getDisplayName());
    player.setShouldBePaused(slot.getDescriptor().getShouldBePaused());
    player.setCanTimeout(slot.getDescriptor().getCanTimeout());

    if (slot.isEmpty()) // needed for forced step, if client crashes before joining room
    {
      logger.warn("PlayerSlot is empty! Was this  Caused by a forced STEP?");
      slot.setClient(new DummyClient());
    }

    slot.setPlayer(player); // set player in role of slot
    slot.getClient().send(new JoinGameProtocolMessage(getId()));
  }

  /**
   * Returns true, if game was prepared and all slots are in use or maxplayercount of game
   * (or any new attribute for readiness) is reached
   *
   * @return true, if two PlayerSlots are filled
   */
  private boolean isReady() {
    if (isPrepared()) {
      for (PlayerSlot slot : this.playerSlots) {
        if (slot.isEmpty()) {
          return false;
        }
      }

      return true;
    } else {
      return this.playerSlots.size() == getMaximumPlayerCount();
    }
  }

  /**
   * Starts game, if gameStatus isn't over or
   *
   * @throws RescuableClientException
   */
  private void startIfReady() throws RescuableClientException {
    logger.debug("startIfReady called");
    if (isOver()) {
      logger.warn("Game is already over.");
      return;
    }

    if (!isReady()) {
      // normally called, when only the first player has connected
      logger.info("Game isn't ready yet.");
      return;
    }

    start();
  }

  /**
   * If the Game is prepared, sync all slots
   *
   * @throws RescuableClientException
   */
  private void start() throws RescuableClientException {
    if (isPrepared()) // sync slots for prepared game. This was already called for PlayerSlots in a game created by a join
    {
      for (PlayerSlot slot : this.playerSlots) {
        // creates players in gameState and sets their attributes
        syncSlot(slot);
      }
    }

    setStatus(GameStatus.ACTIVE);
    this.game.start();

    logger.info("Started the game.");
  }

  /**
   * Get the number of players allowed in the game
   *
   * @return number of allowed players
   */
  private int getMaximumPlayerCount() {
    return 2;
  }

  /**
   * Returns the list of slots (correct ordering).
   *
   * @return all PlayerSlots as unmodifiable list
   */
  public List<PlayerSlot> getSlots() {
    return Collections.unmodifiableList(this.playerSlots);
  }

  /**
   * Threadsafe method to Reserve Slots for the player
   *
   * @return a List of unique IDs
   */
  public synchronized List<String> reserveAllSlots() {
    List<String> result = new ArrayList<>(this.playerSlots.size());

    for (PlayerSlot playerSlot : this.playerSlots) {
      result.add(playerSlot.reserve());
    }

    return result;
  }

  /**
   * Received new data from player and execute data in game
   *
   * @param source Client which caused the event
   * @param data   ProtocolMessage which caused the event
   *
   * @throws RescuableClientException
   */
  public synchronized void onEvent(Client source, ProtocolMessage data) throws RescuableClientException, InvalidGameStateException {
    if (isOver())
      throw new RescuableClientException("Game is already over, but got data: " + data.getClass());

    try {
      this.game.onAction(resolvePlayer(source), data);
    } catch (InvalidMoveException e) {
      this.observerBroadcast(new RoomPacket(this.id, new ProtocolErrorMessage(e.move, e.getMessage())));
      this.game.onPlayerLeft(resolvePlayer(source), ScoreCause.RULE_VIOLATION);
      throw new GameLogicException(e.toString());
    }
  }

  /**
   * Getter for player out of all playerRoles
   *
   * @param source Client to find corresponding Player to
   *
   * @return Player instance
   *
   * @throws RescuableClientException
   */
  private Player resolvePlayer(Client source)
          throws RescuableClientException {
    for (PlayerRole role : getPlayers()) {
      if (role.getClient().equals(source)) {
        Player resolvedPlayer = role.getPlayer();

        if (resolvedPlayer == null) {
          throw new RescuableClientException(
                  "Game isn't ready. Please wait before sending messages.");
        }

        return resolvedPlayer;
      }
    }

    throw new RescuableClientException("Client is not a member of game "
            + this.id);
  }

  /**
   * Get {@link PlayerRole Players} that occupy a slot
   *
   * @return List of PlayerRole Objects
   */
  private Collection<PlayerRole> getPlayers() {
    ArrayList<PlayerRole> clients = new ArrayList<>();
    for (PlayerSlot slot : this.playerSlots) {
      if (!slot.isEmpty()) {
        clients.add(slot.getRole());
      }
    }
    return clients;
  }

  /**
   * Get Server {@link IClient Clients} of all {@link PlayerRole Players}
   *
   * @return all Clients of the Players
   */
  public Collection<IClient> getClients() {
    ArrayList<IClient> clients = new ArrayList<>();
    for (PlayerRole slot : getPlayers()) {
      clients.add(slot.getClient());
    }
    return clients;
  }

  /**
   * Add a Server {@link Client Client} in the role of an Observer
   *
   * @param source Client to be added
   */
  public void addObserver(Client source) {
    ObserverRole role = new ObserverRole(source, this);
    source.addRole(role);
    this.observers.add(role);
    source.send(new ObservationProtocolMessage(getId()));
  }

  /**
   * Pause or un-pause a game
   *
   * @param pause true if game is to be paused
   */
  public synchronized void pause(boolean pause) {
    if (isOver()) {
      logger.warn("Game is already over and can't be paused.");
    }

    // Unnecessary Pause event
    if (pause == isPauseRequested()) {
      logger.warn("Dropped unnecessary PAUSE toggle from {} to {}.", isPauseRequested(), pause);
      return;
    }

    logger.info("Switching PAUSE from {} to {}.", isPauseRequested(), pause);
    this.pauseRequested = pause;
    RoundBasedGameInstance<Player> pausableGame = (RoundBasedGameInstance<Player>) this.game;
    // pause game after current turn has finished
    pausableGame.setPauseMode(pause);

    // continue execution
    if (!isPauseRequested()) {
      pausableGame.afterPause();
    }
  }

  /**
   * @param forced If true, game will be started even if there are not enough
   *               players to complete the game. This should result in a
   *               GameOver.
   *
   * @throws RescuableClientException
   */
  public synchronized void step(boolean forced)
          throws RescuableClientException {
    if (this.status == GameStatus.CREATED) {
      if (forced) {
        logger.warn("Forcing a game to start.");
        start();
      } else {
        logger.warn("Game isn't active yet, step was not forced.");
      }

      return;
    }
    if (isPauseRequested()) {
      logger.info("Stepping.");
      ((RoundBasedGameInstance<Player>) this.game).afterPause();
    } else {
      logger.warn("Can't step if the game is not paused.");
    }
  }

  /** Kick all players, destroy the game and remove it from the manager */
  public void cancel() {
    if (!isOver()) {
      kickAllClients();
      setStatus(GameStatus.OVER);
    }
    this.game.destroy();
    this.gameRoomManager.remove(this);
  }

  /**
   * Broadcast to all observers, that the game is paused
   *
   * @param nextPlayer Player to do the next move
   */
  @Override
  public void onPaused(Player nextPlayer) {
    observerBroadcast(new RoomPacket(getId(), new GamePausedEvent(nextPlayer)));
  }

  /**
   * Set descriptors of PlayerSlots
   */
  public void openSlots(SlotDescriptor[] descriptors)
          throws TooManyPlayersException {
    if (descriptors.length > getMaximumPlayerCount()) {
      throw new TooManyPlayersException();
    }
    this.playerSlots.add(new PlayerSlot(this));
    this.playerSlots.add(new PlayerSlot(this));

    for (int i = 0; i < descriptors.length; i++) {
      this.playerSlots.get(i).setDescriptor(descriptors[i]);
      if (descriptors[i].getShouldBePaused()) {
        pause(true);
      }
    }
  }

  /**
   * If game is prepared return true
   *
   * @return true if Game is prepared
   */
  public boolean isPrepared() {
    return this.prepared;
  }

  /**
   * Return true if GameStatus is OVER
   *
   * @return true if Game is over
   */
  public boolean isOver() {
    return getStatus() == GameStatus.OVER;
  }

  /**
   * Return whether or not the game is paused or will be paused in the next turn.
   * Refer to {@link RoundBasedGameInstance#isPaused()} for the current value
   *
   * @return true, if game is paused
   */
  public boolean isPauseRequested() {
    return this.pauseRequested;
  }

  /**
   * Get the current status of the Game
   *
   * @return status of Game e.g. OVER, CREATED, ...
   */
  public GameStatus getStatus() {
    return this.status;
  }

  /**
   * Update the {@link GameStatus status} of current Game
   *
   * @param status status to be set
   */
  protected void setStatus(GameStatus status) {
    logger.info("Updating Status to {} (was: {})", status, getStatus());
    this.status = status;
  }

  /**
   * Remove specific player by calling {@link IGameInstance#onPlayerLeft(Player) onPlayerLeft(player)}
   *
   * @param player to be removed
   */
  public void removePlayer(Player player, XStreamClient.DisconnectCause cause) {
    logger.info("Removing {} from {}", player, this);
    this.game.onPlayerLeft(player, cause == XStreamClient.DisconnectCause.DISCONNECTED ? ScoreCause.REGULAR : null);
  }

  /**
   * Get the saved {@link GameResult result}
   *
   * @return GameResult
   */
  public GameResult getResult() {
    return this.result;
  }

}
