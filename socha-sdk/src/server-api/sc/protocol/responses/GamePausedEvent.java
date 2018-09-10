package sc.protocol.responses;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import sc.framework.plugins.Player;

@XStreamAlias(value = "paused")
public class GamePausedEvent implements ProtocolMessage{
  
  private final Player nextPlayer;

  /** might be needed by XStream */
  public GamePausedEvent() {
    nextPlayer = null;
  }

  public GamePausedEvent(Player nextPlayer) {
    this.nextPlayer = nextPlayer;
  }

  public Player getNextPlayer() {
    return this.nextPlayer;
  }

}
