package sc.networking.clients;

import sc.api.plugins.IGameState;
import sc.protocol.responses.ProtocolErrorMessage;
import sc.shared.GameResult;

public interface IHistoryListener {
  void onNewState(String roomId, IGameState o);

  void onGameOver(String roomId, GameResult o);

  void onGameError(String roomId, ProtocolErrorMessage error);
}
