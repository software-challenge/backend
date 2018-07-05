package sc.plugin2019;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.shared.PlayerColor;

import static sc.plugin2019.FieldState.*;

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
  private FieldState state;
  
  public Field(int x, int y) {
    this.x = x;
    this.y = y;
    this.state = EMPTY;
  }

  public Field(int x, int y, FieldState state) {
    this(x,y);
    this.state = state;
  }

  public Field(int x, int y, PlayerColor piranha) {
    this(x,y);
    if (piranha == PlayerColor.RED)
      this.state = RED;
    else
      this.state = BLUE;
  }

  public Field(int x, int y, boolean isObstructed) {
    this(x,y);
    if (isObstructed){
      this.state = OBSTRUCTED;
    } else {
      this.state = EMPTY;
    }
  }
  
  @Override
  public Field clone() {
    return new Field(this.x, this.y, this.state);
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
    if (state == RED)
      return PlayerColor.RED;
    else if (state == BLUE)
      return PlayerColor.BLUE;

    return null;
  }

  /**
   * Nur für den Server (für Test) relevant.
   * @param piranha Farbe des Piranhas (null falls leer)
   */
  public void setPiranha(PlayerColor piranha) {
    if (piranha == PlayerColor.RED){
      state = RED;
    } else if (piranha == PlayerColor.BLUE) {
      state = BLUE;
    } else {
      state = EMPTY;
    }
  }

  public boolean isObstructed() {
    return state == OBSTRUCTED;
  }

  public FieldState getState(){
    return state;
  }

  @Override
  public String toString(){
    StringBuilder builder = new StringBuilder();
    builder.append(super.toString());
    builder.append("{x:");
    builder.append(getX());
    builder.append(", y:");
    builder.append(getY());
    builder.append(", state:");
    builder.append(state);
    builder.append("}");
    return builder.toString();
  }

  public void setState(FieldState state) {
    this.state = state;
  }
}
