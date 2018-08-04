package sc.protocol.responses;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import sc.api.plugins.IGameState;
import sc.framework.plugins.IPerspectiveProvider;


/** Wrapper for two objects: state and perspective */
@XStreamAlias("memento")
public final class MementoPacket implements ProtocolMessage, IPerspectiveProvider {
  private IGameState state;

  @XStreamOmitField
  private Object perspective;

  public MementoPacket(IGameState state, Object perspective) {
    this.state = state;
    this.perspective = perspective;
  }

  /** might be needed by XStream */
  public MementoPacket() {
  }

  public IGameState getState() {
    return this.state;
  }

  @Override
  public Object getPerspective() {
    return this.perspective;
  }

}
