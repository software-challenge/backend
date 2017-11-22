package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("timeout")
public class ControlTimeoutRequest {
  @XStreamAsAttribute
  public String roomId;

  @XStreamAsAttribute
  public boolean activate;

  @XStreamAsAttribute
  public int slot;


}
