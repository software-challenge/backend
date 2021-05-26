package sc.server.gaming;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGameState;
import sc.api.plugins.IMove;
import sc.api.plugins.exceptions.GameException;
import sc.api.plugins.exceptions.GameLogicException;
import sc.api.plugins.exceptions.GameRoomException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.api.plugins.host.IGameListener;
import sc.framework.HelperMethods;
import sc.framework.plugins.AbstractGame;
import sc.framework.plugins.Player;
import sc.framework.HelperMethods;
import sc.networking.XStreamProvider;
import sc.networking.clients.IClient;
import sc.networking.clients.LobbyClient;
import sc.networking.clients.ObservingClient;
import sc.networking.clients.XStreamClient;
import sc.protocol.ProtocolPacket;
import sc.protocol.RemovedFromGame;
import sc.protocol.responses.JoinedRoomResponse;
import sc.protocol.responses.ObservationResponse;
import sc.protocol.room.*;
import sc.server.Configuration;
import sc.server.network.Client;
import sc.shared.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A wrapper for an actual <code>GameInstance</code>. GameInstances are provided
 * by the plugins. Additional mapping data (Client2Player) will be stored here.
 */
public class GameRoom implements IGameListener {
  private static final Logger logger = LoggerFactory.getLogger(GameRoom.class);

  private final String id;
  private final GameRoomManager gameRoomManager;
  private final ScoreDefinition scoreDefinition;
  private final List<PlayerSlot> playerSlots = new ArrayList<>(getMaximumPlayerCount());
  private GameStatus status = GameStatus.CREATED;
  private GameResult result = null;
  private boolean pauseRequested = false;
  private List<RoomMessage> history = new ArrayList<>();

  public final IGameInstance game; // TODO make inaccessible
  public final List<IClient> observers = new ArrayList<>();

  public enum GameStatus {
    CREATED, ACTIVE, OVER
  }

  public GameRoom(String id, GameRoomManager gameRoomManager, ScoreDefinition scoreDefinition, IGameInstance game) {
    this.id = id;
    // TODO the GameRoom shouldn't need to know its manager
    this.gameRoomManager = gameRoomManager;
    this.scoreDefinition = scoreDefinition;
    this.game = game;
    game.addGameListener(this);
  }

  /** Generate GameResult, set status to OVER and close the room. */
  @Override
  public synchronized void onGameOver(Map<Player, PlayerScore> results) {
    if (isOver()) {
      logger.warn("{} received an extra GameOver-Event", game);
      return;
    }

    setStatus(GameStatus.OVER);
    try {
      result = generateGameResult(results);
      logger.info("{} is over (regular={})", game, result.isRegular());
      // save playerScore if test mode enabled
      if (Boolean.parseBoolean(Configuration.get(Configuration.TEST_MODE))) {
        List<Player> players = game.getPlayers();
        gameRoomManager.addResultToScore(this.getResult(), players.get(0).getDisplayName(), players.get(1).getDisplayName());
      }
      broadcast(result);
    } catch (Throwable t) {
      logger.error("Failed to generate GameResult from " + results, t);
    }

    if (Boolean.parseBoolean(Configuration.get(Configuration.SAVE_REPLAY))) {
      try {
        saveReplay(new BufferedWriter(new FileWriter(createReplayFile())));
      } catch (IOException e) {
        logger.error("Failed to save replay", e);
      }
    }

    kickAllClients();
    cancel();
  }

  public File createReplayFile() throws IOException {
    String fileName = HelperMethods.getReplayFilename(this.game.getPluginUUID(),
        playerSlots.stream().map(it -> it.getPlayer().getDisplayName()).collect(Collectors.toList()));

    File file = new File(fileName);
    if(file.getParentFile().mkdirs())
      if(file.createNewFile())
        return file;
    throw new IOException("Couldn't create replay file " + file);
  }

  /**
   * Generate scores from results parameter and return GameResult.
   *
   * @return GameResult containing ordered PlayerScores and winners.
   */
  private GameResult generateGameResult(Map<Player, PlayerScore> results) {
    List<PlayerScore> scores = new ArrayList<>();

    // restore order
    for (PlayerSlot player : playerSlots) {
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

  /** Write replay of game to a writer. */
  public void saveReplay(Writer writer) throws IOException {
    XStream xStream = XStreamProvider.loadPluginXStream();

    writer.write("<protocol>\n");
    for (RoomMessage element : history) {
      if (!(element instanceof IGameState))
        continue;
      IGameState state = (IGameState) element;
      // TODO do we need to save RoomPackets?
      RoomPacket roomPacket = createRoomPacket(new MementoMessage(state, null));
      writer.write(xStream.toXML(roomPacket) + "\n");
      writer.flush();
    }

    writer.write(xStream.toXML(createRoomPacket(this.result)) + "\n");
    writer.write("</protocol>");
    writer.close();
  }

  /** Send the given message to all Players and Observers in this room. */
  private void broadcast(ObservableRoomMessage message) {
    broadcast(createRoomPacket(message));
  }

  /** Send ProtocolMessage to all listeners. */
  private void broadcast(ProtocolPacket packet) {
    playerSlots.forEach(slot -> slot.getClient().send(packet));
    observers.forEach(observer -> observer.send(packet));
  }

  /** Send Message to all registered Observers. */
  private void observerBroadcast(ObservableRoomMessage message) {
    observers.forEach(observer -> observer.send(createRoomPacket(message)));
  }

  /** {@link GameRoom#broadcast(ProtocolPacket) Broadcast} a {@link RemovedFromGame} packet to everyone in this room. */
  private void kickAllClients() {
    logger.debug("Kicking clients and observers");
    broadcast(new RemovedFromGame(getId()));
  }

  /** Send updated GameState to all players and observers. */
  @Override
  public void onStateChanged(IGameState data, boolean observersOnly) {
    history.add(data);
    observerBroadcast(new MementoMessage(data, null));
    if (!observersOnly)
      sendStateToPlayers(data);
  }

  /**
   * {@link GameRoom#broadcast(ProtocolPacket) Broadcast} the error package to this room.
   *
   * @param errorPacket ProtocolErrorMessage
   */
  public void onClientError(ErrorMessage errorPacket) {
    broadcast(errorPacket);
  }

  /** Sends the given GameState to all Players. */
  private void sendStateToPlayers(IGameState data) {
    playerSlots.forEach(slot ->
        slot.getClient().send(createRoomPacket(new MementoMessage(data, slot.getPlayer()))));
  }


  /** Create {@link RoomPacket RoomPacket} from id and data Object. */
  public RoomPacket createRoomPacket(RoomMessage data) {
    return new RoomPacket(getId(), data);
  }

  public String getId() {
    return this.id;
  }

  /**
   * Join a client into this room.
   * Starts the game if full.
   *
   * @return true if successfully joined,
   * false if there was no free slot
   */
  public synchronized boolean join(Client client) {
    PlayerSlot slot = playerSlots.stream()
        .filter(PlayerSlot::isFree).findFirst()
        .orElseGet(() -> playerSlots.size() < getMaximumPlayerCount() ? openSlot() : null);
    if(slot == null)
      return false;
    fillSlot(slot, client);
    return true;
  }

  /**
   * If game is not prepared set attributes of PlayerSlot and start game if game is {@link #isReady() ready}.
   *
   * @param openSlot PlayerSlot to fill
   * @param client   Client to fill PlayerSlot
   */
  synchronized void fillSlot(PlayerSlot openSlot, Client client) {
    openSlot.setClient(client); // sets role of Slot as PlayerRole
    client.send(new JoinedRoomResponse(getId()));
    startIfReady();
  }

  /** Returns true if game is full of players. */
  private boolean isReady() {
    return this.playerSlots.size() == getMaximumPlayerCount() && playerSlots.stream().noneMatch(PlayerSlot::isEmpty);
  }

  /** Starts game if ready and not over. */
  private void startIfReady() {
    logger.debug("startIfReady called");
    if (isOver()) {
      logger.warn("Game already over: {}", game);
      return;
    }

    if (!isReady()) {
      // normally called, when only the first player has connected
      logger.info("Game not ready yet: {}", game);
      return;
    }

    start();
  }

  private synchronized void start() {
    setStatus(GameStatus.ACTIVE);
    this.game.start();
    logger.info("Started {}", game);
  }

  /** Get the number of players allowed in the game. */
  private int getMaximumPlayerCount() {
    return 2;
  }

  /** Returns the list of slots (ordered). */
  public List<PlayerSlot> getSlots() {
    return Collections.unmodifiableList(this.playerSlots);
  }

  private PlayerSlot openSlot() {
    if (playerSlots.size() >= getMaximumPlayerCount())
      throw new TooManyPlayersException(this);
    PlayerSlot slot = new PlayerSlot(this);
    Player player = game.onPlayerJoined();
    slot.setPlayer(player);
    this.playerSlots.add(slot);
    return slot;
  }

  /**
   * Threadsafe method to reserve all PlayerSlots.
   *
   * @return list of reservations
   */
  public synchronized List<String> reserveSlots(SlotDescriptor[] descriptors) {
    List<String> result = new ArrayList<>(this.playerSlots.size());
    for (SlotDescriptor descriptor : descriptors) {
      PlayerSlot slot = openSlot();
      Player player = slot.getPlayer();
      player.setDisplayName(descriptor.getDisplayName());
      player.setCanTimeout(descriptor.getCanTimeout());
      if (descriptor.getReserved())
        result.add(slot.reserve());
    }
    return result;
  }

  /**
   * Execute received action.
   *
   * @param source Client which caused the event
   * @param move   ProtocolMessage containing the action
   */
  public synchronized void onEvent(Client source, IMove move) throws GameRoomException {
    if (isOver())
      throw new GameException("Game is already over, but got " + move);

    Player player = resolvePlayer(source);
    try {
      game.onAction(player, move);
    } catch (InvalidMoveException e) {
      final String error = String.format("Ungueltiger Zug von '%s'.\n%s", player.getDisplayName(), e);
      logger.error(error);
      player.setViolationReason(e.getMessage());
      ErrorMessage errorMessage = new ErrorMessage(move, error);
      player.notifyListeners(errorMessage);
      observerBroadcast(errorMessage);
      history.add(errorMessage);
      game.onPlayerLeft(player, ScoreCause.RULE_VIOLATION);
      throw new GameLogicException(e.toString(), e);
    } catch (GameLogicException e) {
      player.notifyListeners(new ErrorMessage(move, e.getMessage()));
      throw e;
    }
  }

  /** Finds player matching the given client. */
  private Player resolvePlayer(Client client) throws GameRoomException {
    Player resolvedPlayer =
        playerSlots.stream()
            .filter(slot -> client.equals(slot.getClient()))
            .map(PlayerSlot::getPlayer)
            .findAny()
            .orElseThrow(() -> new GameRoomException("Client is not a member of game " + this.id));
    if (resolvedPlayer == null)
      throw new GameException("Game isn't ready. Please wait before sending messages.");
    return resolvedPlayer;
  }

  /** Get Server {@link IClient Clients} of all {@link PlayerSlot Players}. */
  public Collection<IClient> getClients() {
    return playerSlots.stream()
        .filter(p -> !p.isEmpty())
        .map(PlayerSlot::getClient)
        .collect(Collectors.toList());
  }

  /** Add a Server {@link Client Client} in the role of an Observer. */
  public void addObserver(Client source) {
    this.observers.add(source);
    source.send(new ObservationResponse(getId()));
  }

  /**
   * Pause or un-pause a game.
   *
   * @param pause true if game is to be paused
   */
  public synchronized void pause(boolean pause) {
    if (isOver()) {
      logger.warn("Can't set pause to {} for already finished {}", pause, game);
      return;
    }

    if (pause == isPauseRequested()) {
      logger.warn("PAUSE is already {}, dropping request", pause);
      return;
    }

    logger.info("Toggling PAUSE from {} to {}", isPauseRequested(), pause);
    this.pauseRequested = pause;
    AbstractGame<?> pausableGame = (AbstractGame<?>) this.game;
    // pause game after current turn has finished
    pausableGame.setPaused(pause);

    // continue execution
    if (!pause && status == GameStatus.ACTIVE) {
      pausableGame.afterPause();
    }
  }

  /**
   * Execute one turn on a paused Game.
   *
   * @param forced If true, the game will be forcibly started if starting
   *               conditions are not met. This should result in a GameOver.
   */
  public synchronized void step(boolean forced) throws GameRoomException {
    if (this.status == GameStatus.CREATED) {
      if (forced) {
        logger.warn("Forcing game start for {}", game);
        start();
      } else {
        logger.info("Game isn't active yet, step was not forced.");
      }

      return;
    }
    if (isPauseRequested()) {
      logger.info("Stepping {}", game);
      ((AbstractGame<?>) game).afterPause();
    } else {
      logger.warn("Can't step unpaused {}", game);
    }
  }

  /** Kick all players, destroy the game and remove it from the manager. */
  public void cancel() {
    if (!isOver()) {
      kickAllClients();
      setStatus(GameStatus.OVER);
    }
    this.game.destroy();
    this.gameRoomManager.remove(this);
  }

  /**
   * Broadcast to all observers that the game is paused.
   *
   * @param nextPlayer Player who comes next after the pause
   */
  @Override
  public void onPaused(Player nextPlayer) {
    observerBroadcast(new GamePaused(nextPlayer));
  }

  /** Return true if GameStatus is OVER. */
  public boolean isOver() {
    return getStatus() == GameStatus.OVER;
  }

  /** Return whether or not the game is paused or will be paused in the next turn. */
  public boolean isPauseRequested() {
    return this.pauseRequested;
  }

  /** @return current status of the Game. */
  public GameStatus getStatus() {
    return this.status;
  }

  /** Update the {@link GameStatus status} of the current Game. */
  protected void setStatus(GameStatus status) {
    logger.info("Updating Status to {} (was: {})", status, getStatus());
    this.status = status;
  }

  /** Remove a player by calling {@link IGameInstance#onPlayerLeft(Player, ScoreCause) onPlayerLeft}. */
  public void removePlayer(Player player, XStreamClient.DisconnectCause cause) {
    logger.info("Removing {} from {}", player, this);
    this.game.onPlayerLeft(player, cause == XStreamClient.DisconnectCause.DISCONNECTED ? ScoreCause.REGULAR : null);
  }

  /** Get the saved {@link GameResult result}. */
  public GameResult getResult() {
    return this.result;
  }

}
