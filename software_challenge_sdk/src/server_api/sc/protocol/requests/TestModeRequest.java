package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.protocol.responses.ProtocolMessage;

/**
 * Send by administrative client to enable or disable testMode
 */

@XStreamAlias("testMode")
public class TestModeRequest extends ProtocolMessage  implements ILobbyRequest {


  @XStreamAsAttribute
  public boolean testMode;

  public TestModeRequest(boolean testMode) {
    this.testMode = testMode;
  }
}
