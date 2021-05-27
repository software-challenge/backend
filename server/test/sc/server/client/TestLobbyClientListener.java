package sc.server.client;

import sc.api.plugins.IGameState;
import sc.framework.plugins.Player;
import sc.networking.clients.ILobbyClientListener;
import sc.protocol.room.RoomMessage;
import sc.protocol.responses.GamePreparedResponse;
import sc.protocol.responses.ErrorPacket;
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
  public ErrorPacket errorResponse;
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
    this.roomId = roomId;
    this.newState = state;
    newStateReceived = true;
  }

  @Override
  public void onError(ErrorPacket error) {
    this.errorResponse = error;
    errorReceived = true;
  }

  @Override
  public void onRoomMessage(String roomId, RoomMessage data) {
    this.roomId = roomId;
    this.roomMessage = data;
    roomMessageReceived = true;
  }

  @Override
  public void onGamePrepared(GamePreparedResponse response) {
    this.prepareGameResponse = response;
    this.roomId = response.getRoomId();
    gamePreparedReceived = true;
  }

  @Override
  public void onGameLeft(String roomId) {
    this.roomId = roomId;
    gameLeftReceived = true;
  }

  @Override
  public void onGameJoined(String roomId) {
    this.roomId = roomId;
    gameJoinedReceived = true;
  }

  @Override
  public void onGameOver(String roomId, GameResult data) {
    this.roomId = roomId;
    this.result = data;
    gameOverReceived = true;
  }

  @Override
  public void onGameObserved(String roomId) {
    this.roomId = roomId;
    observedReceived = true;
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
