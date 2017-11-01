package sc.server.client;

import sc.framework.plugins.SimplePlayer;
import sc.networking.clients.ILobbyClientListener;
import sc.protocol.responses.ProtocolErrorMessage;
import sc.protocol.responses.PrepareGameProtocolMessage;
import sc.shared.GameResult;

public class TestLobbyClientListener implements ILobbyClientListener {

  public boolean onObserved = false;
  public boolean onGamePaused = false;
  public boolean onGameOver = false;
  public boolean onGameJoined = false;
  public boolean onGameLeft = false;
  public boolean onGamePrepared = false;
  public boolean onRoomMessage = false;
  public boolean onError = false;
  public boolean onNewState = false;

  public String roomid;
  public SimplePlayer player;
  public GameResult result;
  public PrepareGameProtocolMessage prepareGameResponse;
  public Object roomMessage;
  public ProtocolErrorMessage errorResponse;
  public Object newState;

  @Override
  public boolean equals(Object obj) {
    if (! (obj instanceof TestLobbyClientListener)) return false;

    TestLobbyClientListener o = (TestLobbyClientListener)obj;
    return onObserved == o.onObserved &&
            onGamePaused == o.onGamePaused &&
            onGameOver == o.onGameOver &&
            onGameJoined == o.onGameJoined &&
            onGameLeft == o.onGameLeft &&
            onGamePrepared == o.onGamePrepared &&
            onRoomMessage == o.onRoomMessage &&
            onError == o.onError &&
            onNewState == o.onNewState &&
            roomMessage != null &&
            roomMessage.equals(o.roomid);
  }

  @Override
  public void onNewState(String roomId, Object state) {
    onNewState = true;
    this.roomid = roomId;
    this.newState = state;
  }

  @Override
  public void onError(String roomId, ProtocolErrorMessage error) {
    onError = true;
    this.roomid = roomId;
    this.errorResponse = error;
  }

  @Override
  public void onRoomMessage(String roomId, Object data) {
    onRoomMessage = true;
    this.roomid = roomId;
    this.roomMessage = data;
  }

  @Override
  public void onGamePrepared(PrepareGameProtocolMessage response) {
    onGamePrepared = true;
    this.prepareGameResponse = response;
  }

  @Override
  public void onGameLeft(String roomId) {
    onGameLeft = true;
    this.roomid = roomId;
  }

  @Override
  public void onGameJoined(String roomId) {
    onGameJoined = true;
    this.roomid = roomId;
  }

  @Override
  public void onGameOver(String roomId, GameResult data) {
    onGameOver = true;
    this.roomid = roomId;
    this.result = data;
  }

  @Override
  public void onGamePaused(String roomId, SimplePlayer nextPlayer) {
    onGamePaused = true;
    this.roomid = roomId;
    this.player = nextPlayer;
  }

  @Override
  public void onGameObserved(String roomId) {
    onObserved = true;
    this.roomid = roomId;

  }
}
