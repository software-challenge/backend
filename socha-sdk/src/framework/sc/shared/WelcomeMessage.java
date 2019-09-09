package sc.shared;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.protocol.responses.ProtocolMessage;

/** Nachricht, die zu Beginn eines Spiels an einen Client geschickt wird, um ihm seine Spielerfarbe mitzuteilen. */
@XStreamAlias(value = "welcomeMessage")
public class WelcomeMessage implements ProtocolMessage {
  
  @XStreamAsAttribute
  private String color;
  
  /** might be needed by XStream */
  public WelcomeMessage() {
  }
  
  public WelcomeMessage(PlayerColor c) {
    color = c.toString().toLowerCase();
  }
  
  public PlayerColor getPlayerColor() {
    return PlayerColor.valueOf(color.toUpperCase());
  }

  @Override
  public String toString() {
    return "WelcomeMessage{" +
            "color='" + color + '\'' +
            '}';
  }
}
