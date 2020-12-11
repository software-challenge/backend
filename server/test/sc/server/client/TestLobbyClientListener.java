package sc.server.client;

import sc.api.plugins.IGameState;
import sc.framework.plugins.Player;
import sc.networking.clients.ILobbyClientListener;
import sc.protocol.responses.GamePreparedResponse;
import sc.protocol.responses.ProtocolErrorMessage;
import sc.protocol.responses.ProtocolMessage;
import sc.shared.GameResult;

public class TestLobbyClientListener implements ILobbyClientListener {

  public boolean observedReceived = false;
  public boolean gamePausedReceived = false;
  public boolean gameOverReceived = false;
  public boolean gameJoinedReceived = false;
  public boolean gameLeftReceived = false;
  public boolean gamePreparedReceived = false;
  public boolean roomMessageReceived = false;
  public boolean errorReceived = false;
  public boolean newStateReceived = false;

  public String roomId;
  public Player player;
  public GameResult result;
  public GamePreparedResponse prepareGameResponse;
  public Object roomMessage;
  public ProtocolErrorMessage errorResponse;
  public Object newState;

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof TestLobbyClientListener)) return false;

    TestLobbyClientListener o = (TestLobbyClientListener) obj;
    return observedReceived == o.observedReceived &&
            gamePausedReceived == o.gamePausedReceived &&
            gameOverReceived == o.gameOverReceived &&
            gameJoinedReceived == o.gameJoinedReceived &&
            gameLeftReceived == o.gameLeftReceived &&
            gamePreparedReceived == o.gamePreparedReceived &&
            roomMessageReceived == o.roomMessageReceived &&
            errorReceived == o.errorReceived &&
            newStateReceived == o.newStateReceived &&
            roomMessage != null &&
            roomMessage.equals(o.roomId);
  }

  @Override
  public void onNewState(String roomId, IGameState state) {
    newStateReceived = true;
    this.roomId = roomId;
    this.newState = state;
  }

  @Override
  public void onError(String roomId, ProtocolErrorMessage error) {
    errorReceived = true;
    this.roomId = roomId;
    this.errorResponse = error;
  }

  @Override
  public void onRoomMessage(String roomId, ProtocolMessage data) {
    roomMessageReceived = true;
    this.roomId = roomId;
    this.roomMessage = data;
  }

  @Override
  public void onGamePrepared(GamePreparedResponse response) {
    gamePreparedReceived = true;
    this.prepareGameResponse = response;
  }

  @Override
  public void onGameLeft(String roomId) {
    gameLeftReceived = true;
    this.roomId = roomId;
  }

  @Override
  public void onGameJoined(String roomId) {
    gameJoinedReceived = true;
    this.roomId = roomId;
  }

  @Override
  public void onGameOver(String roomId, GameResult data) {
    gameOverReceived = true;
    this.roomId = roomId;
    this.result = data;
  }

  @Override
  public void onGamePaused(String roomId, Player nextPlayer) {
    gamePausedReceived = true;
    this.roomId = roomId;
    this.player = nextPlayer;
  }

  @Override
  public void onGameObserved(String roomId) {
    observedReceived = true;
    this.roomId = roomId;
  }

  @Override
  public String toString() {
    return "TestLobbyClientListener{" +
            "observedReceived=" + observedReceived +
            ", gamePausedReceived=" + gamePausedReceived +
            ", gameOverReceived=" + gameOverReceived +
            ", gameJoinedReceived=" + gameJoinedReceived +
            ", gameLeftReceived=" + gameLeftReceived +
            ", gamePreparedReceived=" + gamePreparedReceived +
            ", roomMessageReceived=" + roomMessageReceived +
            ", errorReceived=" + errorReceived +
            ", newStateReceived=" + newStateReceived +
            ", roomId='" + roomId + '\'' +
            ", player=" + player +
            ", result=" + result +
            ", prepareGameResponse=" + prepareGameResponse +
            ", roomMessage=" + roomMessage +
            ", errorResponse=" + errorResponse +
            ", newState=" + newState +
            '}';
  }
}
