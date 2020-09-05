package sc.networking.clients;

import sc.api.plugins.IGameState;
import sc.framework.plugins.Player;
import sc.protocol.responses.ProtocolErrorMessage;
import sc.protocol.responses.PrepareGameProtocolMessage;
import sc.protocol.responses.ProtocolMessage;
import sc.shared.GameResult;

public interface ILobbyClientListener {

  void onNewState(String roomId, IGameState state);

  void onRoomMessage(String roomId, ProtocolMessage data);

  void onError(String roomId, ProtocolErrorMessage error);

  void onGamePrepared(PrepareGameProtocolMessage response);

  void onGameLeft(String roomId);

  void onGameJoined(String roomId);

  void onGameOver(String roomId, GameResult data);

  void onGamePaused(String roomId, Player nextPlayer);

  void onGameObserved(String roomId);

}
