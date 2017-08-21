package sc.server.client;

import sc.framework.plugins.SimplePlayer;
import sc.networking.clients.IHistoryListener;
import sc.networking.clients.ILobbyClientListener;
import sc.protocol.responses.ErrorResponse;
import sc.protocol.responses.PrepareGameResponse;
import sc.shared.GameResult;

/**
 * Created by nils on 26.07.17.
 */
public class TestObserverListener implements ILobbyClientListener {

  public String roomid;
  @Override
  public void onNewState(String roomId, Object state) {

  }

  @Override
  public void onError(String roomId, ErrorResponse error) {

  }

  @Override
  public void onRoomMessage(String roomId, Object data) {

  }

  @Override
  public void onGamePrepared(PrepareGameResponse response) {

  }

  @Override
  public void onGameLeft(String roomId) {

  }

  @Override
  public void onGameJoined(String roomId) {

  }

  @Override
  public void onGameOver(String roomId, GameResult data) {

  }

  @Override
  public void onGamePaused(String roomId, SimplePlayer nextPlayer) {

  }

  @Override
  public void onGameObserved(String roomId) {
    this.roomid = roomId;
  }
}
