package sc.server.client;

import sc.api.plugins.host.IPlayerListener;
import sc.protocol.responses.ProtocolMessage;

public class PlayerListener implements IPlayerListener{
  public boolean onPlayerEvent;
  @Override
  public void onPlayerEvent(ProtocolMessage request) {
    onPlayerEvent = true;
  }
}
