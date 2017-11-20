package sc.protocol.responses;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.protocol.requests.ILobbyRequest;

/**
 * Response to TestModeRequest containing the current status of testMode
 */

@XStreamAlias(value = "testing")
public class TestModeMessage extends ProtocolMessage implements ILobbyRequest {

  @XStreamAsAttribute
  public boolean testMode;

  public TestModeMessage(boolean testMode) {
    this.testMode = testMode;
  }
}
