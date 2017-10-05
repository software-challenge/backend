package sc.protocol.responses;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.protocol.requests.ILobbyRequest;

@XStreamAlias(value = "testing")
public class TestModeMessage extends ProtocolMessage implements ILobbyRequest {

  @XStreamAsAttribute
  private boolean testMode;

  public TestModeMessage(boolean testMode) {
    this.testMode = testMode;
  }
}
