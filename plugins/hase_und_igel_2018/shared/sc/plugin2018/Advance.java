package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.plugin2018.util.GameRuleLogic;
import sc.shared.InvalidMoveException;

/**
 * Ein Vorwärtszug, um spezifizierte Distanz. Verbrauchte Karotten werden mit k = (distance * (distance + 1)) / 2
 * berechnet (Gaußsche Summe)
 */
@XStreamAlias(value = "advance")
public class Advance extends Action{

  @XStreamAsAttribute
  private int distance;

  public Advance(int distance) {
    super();
    this.distance = distance;
  }

  public Advance(int distance, int order) {
    this.order = order;
    this.distance = distance;
  }

  @Override
  public void perform(GameState state) throws InvalidMoveException {
    if (GameRuleLogic.isValidToAdvance(state, this.distance)) {
      state.getCurrentPlayer().changeCarrotsBy(- GameRuleLogic.calculateCarrots(this.distance));
      state.getCurrentPlayer().setFieldIndex(state.getCurrentPlayer().getFieldIndex() + distance);
      if (state.getTypeAt(state.getCurrentPlayer().getFieldIndex()) == FieldType.HARE) {
        state.getCurrentPlayer().setMustPlayCard(true);
      }
      // Setze letzte Aktion
      state.setLastAction(this);
    } else {
      throw new InvalidMoveException("Vorwärtszug um " + this.distance + " Felder ist nicht möglich");
    }
  }

  /**
   * Gibt das Dinstanzattribut zurück
   * @return Distanz
   */
  public int getDistance() {
    return distance;
  }

  @Override
  public Advance clone() {
    return new Advance(this.distance, this.order);
  }

  @Override
  public boolean equals(Object o) {
    if(o instanceof Advance) {
      return (this.distance == ((Advance) o).distance);
    }
    return false;
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder("Advance: distance ");
    b.append(this.distance);
    b.append(" order ");
    b.append(this.order);
    return b.toString();
  }

}
