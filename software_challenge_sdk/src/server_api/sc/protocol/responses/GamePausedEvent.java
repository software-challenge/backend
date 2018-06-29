package sc.protocol.responses;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import sc.framework.plugins.AbstractPlayer;

@XStreamAlias(value = "paused")
public class GamePausedEvent implements ProtocolMessage{
  
  private final AbstractPlayer nextPlayer;

  /** might be needed by XStream */
  public GamePausedEvent() {
    nextPlayer = null;
  }

  public GamePausedEvent(AbstractPlayer nextPlayer) {
    this.nextPlayer = nextPlayer;
  }

  public AbstractPlayer getNextPlayer() {
    return this.nextPlayer;
  }

}
