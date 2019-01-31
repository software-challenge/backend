package sc.plugin2019;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.api.plugins.IField;
import sc.shared.PlayerColor;

import java.util.Optional;

import static sc.plugin2019.FieldState.*;

/**
 * Ein Feld des Spielfelds. Ein Spielfeld hat eine x- und y-Koordinate und einen {@link FieldState}.
 */
@XStreamAlias(value = "field")
public class Field implements IField {

  @XStreamAsAttribute
  private int x;

  @XStreamAsAttribute
  private int y;

  @XStreamAsAttribute
  private FieldState state;

  public Field(int x, int y, FieldState state) {
    this.x = x;
    this.y = y;
    this.state = state;
  }

  public Field(int x, int y) {
    this(x, y, EMPTY);
  }

  public Field(int x, int y, PlayerColor piranha) {
    this(x, y, FieldState.from(piranha));
  }

  public Field(int x, int y, boolean isObstructed) {
    this(x, y, isObstructed ? OBSTRUCTED : EMPTY);
  }

  public Field(Field fieldToClone) {
    this(fieldToClone.x, fieldToClone.y, fieldToClone.state);
  }

  @Override
  public Field clone() {
    return new Field(this);
  }

  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof Field))
      return false;
    Field field = (Field) obj;
    return x == field.x && y == field.y && state == field.state;
  }

  @Override
  public String toString() {
    return String.format("Field(%d|%d){%s}", x, y, state);
  }

  public int getX() {
    return x;
  }

  void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  void setY(int y) {
    this.y = y;
  }

  public Optional<PlayerColor> getPiranha() {
    if(state == RED)
      return Optional.of(PlayerColor.RED);
    else if(state == BLUE)
      return Optional.of(PlayerColor.BLUE);

    return Optional.empty();
  }

  /**
   * Nur für den Server (für Test) relevant.
   *
   * @param piranha Farbe des Piranhas
   */
  public void setPiranha(PlayerColor piranha) {
    state = FieldState.from(piranha);
  }

  public boolean isObstructed() {
    return state == OBSTRUCTED;
  }

  public FieldState getState() {
    return state;
  }

  public void setState(FieldState state) {
    this.state = state;
  }
}
