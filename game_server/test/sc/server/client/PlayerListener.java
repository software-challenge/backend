package sc.server.client;

import sc.api.plugins.host.IPlayerListener;

public class PlayerListener implements IPlayerListener{
  public boolean onPlayerEvent;
  @Override
  public void onPlayerEvent(Object request) {
    onPlayerEvent = true;
  }
}
