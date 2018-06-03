package sc.plugin2019;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.shared.PlayerColor;

/**
 * Ein Feld des Spielfelds. Ein Spielfeld ist durch den index eindeutig identifiziert.
 * Das type Attribut gibt an, um welchen Feldtyp es sich handelt.
 */
@XStreamAlias(value = "field")
public class Field {

  @XStreamAsAttribute
  private int x;

  @XStreamAsAttribute
  private int y;

  @XStreamAsAttribute
  private PlayerColor piranha;
  
  public Field(int x, int y) {
    this.x = x;
    this.y = y;
  }
  
  public Field(int x, int y, PlayerColor piranha) {
    this(x,y);
    this.piranha = piranha;
  }
  
  @Override
  public Field clone() {
    return new Field(this.x, this.y, this.piranha);
  }

  public int getX() {
    return x;
  }

  /**
   * Nur für den Server (für Test) relevant.
   * @param x x-Koordniate des Feldes
   */
  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  /**
   * Nur für den Server (für Test) relevant.
   * @param y y-Koordinate des Feldes
   */
  public void setY(int y) {
    this.y = y;
  }

  public PlayerColor getPiranha() {
    return piranha;
  }

  /**
   * Nur für den Server (für Test) relevant.
   * @param piranha Farbe des Piranhas (null falls leer)
   */
  public void setPiranha(PlayerColor piranha) {
    this.piranha = piranha;
  }
}
