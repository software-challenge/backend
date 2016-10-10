package sc.plugin2016;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;


/**
 * Stellt eine Leitung dar. Hierbei ist zu beachten, dass eine Leitung immer so behandelt wird, dass die Reihenfolge der Strommasten irrelevant ist.
 * @author niklas
 *
 */
@XStreamAlias(value = "connection")
public class Connection {
	/**
	 * X-Koordinate des ersten Strommast
	 */
  @XStreamAsAttribute
  public int x1;
  /**
   * Y-Koordinate des ersten Strommast
   */
  @XStreamAsAttribute
  public int y1;
  @XStreamAsAttribute
  /**
   * X-Koordinate des zweiten Strommast
   */
  public int x2;
  @XStreamAsAttribute
  /**
   * Y-Koordinate des zweiten Strommast
   */
  public int y2;
  @XStreamAsAttribute
  public PlayerColor owner;
  
  
  /**
   * Stellt eine Leitung zwischen zwei Punkten (x1, y1) und (x2, y2) des Spielers owner dar.
   * @param x1 x-Koordinate des ersten Strommast der Leitung
   * @param y1 y-Koordinate des zweiten Strommast der Leitung
   * @param x2 x-Koordinate des ersten Strommast der Leitung
   * @param y2 y-Koordinate des zweiten Strommast der Leitung
   * @param owner Besitzer der Leitung
   */
  public Connection(int x1, int y1, int x2, int y2, PlayerColor owner) {
    this.x1 = x1;
    this.x2 = x2;
    this.y1 = y1;
    this.y2 = y2;
    this.owner = owner;
  }
  
  /**
   * Vergleichsmethode für Verbindungen. Eine Verbindung mit vertauschtem ersten und zweiten Strommast ist dabei auch gleich.
   */
  @Override  
  public boolean equals(Object o) {
    if(o instanceof Connection) {
      Connection c = (Connection) o;
      if(c.x1 == x1 && c.y1 == y1 && c.x2 == x2 && c.y2 == y2 ||
         c.x1 == x2 && c.y1 == y2 && c.x2 == x1 && c.y2 == y1) {
        return (owner == c.owner);
      }
    }
    return false;
  }
  
  /**
   * Erzeugt eine Deepcopy einer Leitung
   */
  public Connection clone() {
    return new Connection(this.x1, this.y1, this.x2, this.y2, this.owner);
  }
}
