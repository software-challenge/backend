package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.protocol.responses.ProtocolMessage;

/**
 * Send by Client to authenticate as administrator. Is not answered if successful.
 */

@XStreamAlias("authenticate")
public class AuthenticateRequest extends ProtocolMessage implements ILobbyRequest {
  @XStreamAsAttribute
  private String passphrase;

  /**
   * might be needed by XStream
   */
  public AuthenticateRequest() {
  }

  public AuthenticateRequest(String passphrase) {
    this.passphrase = passphrase;
  }

  public String getPassword() {
    return this.passphrase;
  }
}
