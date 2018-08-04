package sc.server.plugins;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.jetbrains.annotations.NotNull;
import sc.api.plugins.host.IPlayerListener;
import sc.framework.plugins.AbstractPlayer;
import sc.shared.PlayerColor;

public class TestPlayer extends AbstractPlayer {
  
  @XStreamAsAttribute
  PlayerColor color;

  public TestPlayer(PlayerColor pc) {
    this.color = pc;
  }

  public void requestMove() {
    TestTurnRequest request = new TestTurnRequest();
    System.out.println(color + " Player is requesting a move");
    notifyListeners(request);
  }

  @NotNull
  @Override
  public AbstractPlayer clone() {
    return null;
  }

  @NotNull
  @Override
  public PlayerColor getPlayerColor() {
    return color;
  }

}
