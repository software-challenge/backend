package sc.server.client;

import sc.api.plugins.IGameState;
import sc.framework.plugins.Player;
import sc.networking.clients.ILobbyClientListener;
import sc.protocol.responses.PrepareGameProtocolMessage;
import sc.protocol.responses.ProtocolErrorMessage;
import sc.protocol.responses.ProtocolMessage;
import sc.shared.GameResult;

public class TestObserverListener implements ILobbyClientListener {

  public String roomid;

  @Override
  public void onGameObserved(String roomId) {
    this.roomid = roomId;
  }

  @Override
  public void onNewState(String roomId, IGameState state) {
  }

  @Override
  public void onError(String roomId, ProtocolErrorMessage error) {
  }

  @Override
  public void onRoomMessage(String roomId, ProtocolMessage data) {
  }

  @Override
  public void onGamePrepared(PrepareGameProtocolMessage response) {
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
  public void onGamePaused(String roomId, Player nextPlayer) {
  }

}
