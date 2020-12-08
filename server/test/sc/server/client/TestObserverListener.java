package sc.server.client;

import sc.networking.clients.AbstractLobbyClientListener;

public class TestObserverListener extends AbstractLobbyClientListener {

  public String roomid;

  @Override
  public void onGameObserved(String roomId) {
    this.roomid = roomId;
  }
}
