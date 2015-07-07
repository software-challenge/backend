package sc.plugin2016;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;


/**
 * Stellt eine Verbindung dar.
 * @author niklas
 *
 */
@XStreamAlias(value = "connection")
public class Connection {
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
  
  public Connection(int x1, int y1, int x2, int y2, PlayerColor owner) {
    this.x1 = x1;
    this.x2 = x2;
    this.y1 = y1;
    this.y2 = y2;
    this.owner = owner;
  }
  
  @Override  
  public boolean equals(Object o) {
    if(o instanceof Connection) {
      Connection c = (Connection) o;
      if(c.x1 == x1 && c.y1 == y1 && c.x2 == x2 && c.y2 == y2 ||
         c.x1 == x2 && c.y1 == y2 && c.x2 == x1 && c.y2 == y1) {
        return true;
      }
    }
    return false;
  }
}
