package sc.networking.clients;

import sc.framework.plugins.AbstractPlayer;
import sc.protocol.responses.ProtocolErrorMessage;
import sc.protocol.responses.PrepareGameProtocolMessage;
import sc.shared.GameResult;

public interface ILobbyClientListener {

  void onNewState(String roomId, Object state);

  void onError(String roomId, ProtocolErrorMessage error);

  void onRoomMessage(String roomId, Object data);

  void onGamePrepared(PrepareGameProtocolMessage response);

  void onGameLeft(String roomId);

  void onGameJoined(String roomId);

  void onGameOver(String roomId, GameResult data);

  void onGamePaused(String roomId, AbstractPlayer nextPlayer);

  void onGameObserved(String roomId);

}
