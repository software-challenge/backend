package sc.protocol.responses;

import sc.framework.plugins.IPerspectiveProvider;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;


/** Wrapper for two objects: state and perspective */
@XStreamAlias("memento")
public final class MementoPacket implements ProtocolMessage, IPerspectiveProvider {
  private Object state;

  @XStreamOmitField
  private Object perspective;

  public MementoPacket(Object state, Object perspective) {
    this.state = state;
    this.perspective = perspective;
  }

  /** might be needed by XStream */
  public MementoPacket() {
    // TODO Auto-generated constructor stub
  }

  public Object getState() {
    return this.state;
  }

  @Override
  public Object getPerspective() {
    return this.perspective;
  }

}
