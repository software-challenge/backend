package sc.server.client;

import sc.api.plugins.host.IPlayerListener;
import sc.protocol.responses.ProtocolMessage;

import java.util.ArrayList;
import java.util.List;

public class PlayerListener implements IPlayerListener {
  public boolean playerEventReceived;
  
  public List<ProtocolMessage> requests = new ArrayList<>();
  
  @Override
  public void onPlayerEvent(ProtocolMessage request) {
    playerEventReceived = true;
    requests.add(request);
  }
  
}
