package sc.server.plugins;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.api.plugins.host.IPlayerListener;
import sc.framework.plugins.AbstractPlayer;
import sc.shared.PlayerColor;

public class TestPlayer extends AbstractPlayer {
  
  @XStreamAsAttribute
  PlayerColor color;

  public TestPlayer(PlayerColor pc) {
    this.color = pc;
  }

  @Override
  public void addPlayerListener(IPlayerListener listener) {
    this.listeners.add(listener);
  }

  @Override
  public void removePlayerListener(IPlayerListener listener) {
    this.listeners.remove(listener);
  }

  public void requestMove() {
    TestTurnRequest request = new TestTurnRequest();
    System.out.println("Player: " + color + " requested a move");

    for (IPlayerListener listener : this.listeners) {
      listener.onPlayerEvent(request);
    }
  }

}
