package sc.server.plugins;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.jetbrains.annotations.NotNull;
import sc.framework.plugins.Player;
import sc.shared.PlayerColor;

public class TestPlayer extends Player {
  
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
  public Player clone() {
    return null;
  }

  @NotNull
  @Override
  public PlayerColor getPlayerColor() {
    return color;
  }

}
