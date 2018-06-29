package sc.protocol.responses;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import sc.framework.plugins.SimplePlayer;

@XStreamAlias(value = "paused")
public class GamePausedEvent extends ProtocolMessage {
  
  private final SimplePlayer nextPlayer;

  /** might be needed by XStream */
  public GamePausedEvent() {
    nextPlayer = null;
  }

  public GamePausedEvent(SimplePlayer nextPlayer) {
    this.nextPlayer = nextPlayer;
  }

  public SimplePlayer getNextPlayer() {
    return this.nextPlayer;
  }

}
