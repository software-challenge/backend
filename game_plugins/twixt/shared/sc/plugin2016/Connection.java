package sc.plugin2016;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;


/**
 * Stellt eine Verbindung dar. Ist nur für die Kommunikation nötig.
 * @author niklas
 *
 */
@XStreamAlias(value = "connection")
public class Connection {
  public Connection(int x1, int y1, int x2, int y2, PlayerColor owner) {
    this.x1 = x1;
    this.x2 = x2;
    this.y1 = y1;
    this.y2 = y2;
    this.owner = owner;
  }
  @XStreamAsAttribute
  public int x1;
  @XStreamAsAttribute
  public int y1;
  @XStreamAsAttribute
  public int x2;
  @XStreamAsAttribute
  public int y2;
  @XStreamAsAttribute
  public PlayerColor owner;
  
  

}
