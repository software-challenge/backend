package sc.server.client;

import sc.framework.plugins.Player;
import sc.networking.clients.ILobbyClientListener;
import sc.protocol.responses.PrepareGameProtocolMessage;
import sc.protocol.responses.ProtocolErrorMessage;
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

  public String roomid;
  public Player player;
  public GameResult result;
  public PrepareGameProtocolMessage prepareGameResponse;
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
            roomMessage.equals(o.roomid);
  }

  @Override
  public void onNewState(String roomId, Object state) {
    newStateReceived = true;
    this.roomid = roomId;
    this.newState = state;
  }

  @Override
  public void onError(String roomId, ProtocolErrorMessage error) {
    errorReceived = true;
    this.roomid = roomId;
    this.errorResponse = error;
  }

  @Override
  public void onRoomMessage(String roomId, Object data) {
    roomMessageReceived = true;
    this.roomid = roomId;
    this.roomMessage = data;
  }

  @Override
  public void onGamePrepared(PrepareGameProtocolMessage response) {
    gamePreparedReceived = true;
    this.prepareGameResponse = response;
  }

  @Override
  public void onGameLeft(String roomId) {
    gameLeftReceived = true;
    this.roomid = roomId;
  }

  @Override
  public void onGameJoined(String roomId) {
    gameJoinedReceived = true;
    this.roomid = roomId;
  }

  @Override
  public void onGameOver(String roomId, GameResult data) {
    gameOverReceived = true;
    this.roomid = roomId;
    this.result = data;
  }

  @Override
  public void onGamePaused(String roomId, Player nextPlayer) {
    gamePausedReceived = true;
    this.roomid = roomId;
    this.player = nextPlayer;
  }

  @Override
  public void onGameObserved(String roomId) {
    observedReceived = true;
    this.roomid = roomId;

  }

}
