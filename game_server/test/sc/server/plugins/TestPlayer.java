package sc.server.plugins;

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import sc.api.plugins.host.IPlayerListener;
import sc.framework.plugins.SimplePlayer;
import sc.shared.PlayerColor;

public class TestPlayer extends SimplePlayer {
  
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
