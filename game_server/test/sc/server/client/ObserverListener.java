package sc.server.client;

import sc.networking.clients.IHistoryListener;
import sc.protocol.responses.ErrorResponse;
import sc.shared.GameResult;

/**
 * Created by nils on 26.07.17.
 */
public class ObserverListener implements IHistoryListener {
  @Override
  public void onNewState(String roomId, Object o) {
    System.out.println("new state");
  }

  @Override
  public void onGameOver(String roomId, GameResult o) {
    System.out.println("game over");
  }

  @Override
  public void onGameError(String roomId, ErrorResponse error) {
    System.out.println("game error");

  }
}
