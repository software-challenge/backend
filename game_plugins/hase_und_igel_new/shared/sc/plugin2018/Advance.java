package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.plugin2018.util.GameUtil;
import sc.shared.InvalidMoveException;

/** TODO comment everything
 * Ein Vorwärtszug, um spezifizierte Distanz. Verbrauchte Karroten werden mit k = (distance * (distance + 1)) / 2
 * berechnet (Gaußsche Summe)
 */
@XStreamAlias(value = "advance")
public class Advance extends Action{

  @XStreamAsAttribute
  private int distance;

  public Advance(int distance) {
    super();
    setDistance(distance);
  }

  public Advance(int distance, int order) {
    this.order = order;
    setDistance(distance);
  }


  @Override
  public void perform(GameState state) throws InvalidMoveException {
    if (GameUtil.isValidToMove(state, this.distance)) {
      state.getCurrentPlayer().changeCarrotsAvailableBy(GameUtil.calculateCarrots(this.distance));
      state.getCurrentPlayer().setFieldNumber(state.getCurrentPlayer().getFieldIndex() + distance);
    } else {
      throw new InvalidMoveException("Vorwärtszug um " + this.distance + " Felder ist nicht möglich");
    }
  }

  public int getDistance() {
    return distance;
  }

  public void setDistance(int distance) {
    if (distance <= 0) {
      throw new IllegalArgumentException("distance has to be greater than 0");
    }
    this.distance = distance;
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
}
