package sc.protocol.responses;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("error")
public class ProtocolErrorMessage extends ProtocolMessage {
  private ProtocolMessage originalRequest;

  @XStreamAsAttribute
  private String message;

  /**
   * might be needed by XStream
   */
  public ProtocolErrorMessage() {
  }

  public ProtocolErrorMessage(ProtocolMessage request, String message) {
    this.originalRequest = request;
    this.message = message;
  }

  public ProtocolMessage getOriginalRequest() {
    return this.originalRequest;
  }

  public String getMessage() {
    return this.message;
  }

}
