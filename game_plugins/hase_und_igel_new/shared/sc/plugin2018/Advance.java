package sc.plugin2018;

import sc.plugin2018.util.GameUtil;
import sc.shared.InvalidMoveException;

/** TODO comment everything
 * Ein Vorwärtszug, um spezifizierte Distanz. Verbrauchte Karroten werden mit k = (distance * (distance + 1)) / 2
 * berechnet (Gaußsche Summe)
 */
public class Advance extends Action{


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
  public void perform(GameState state, Player player) throws InvalidMoveException {
    // check whether field can be
    int neededCarrots = GameUtil.calculateCarrots(this.distance);
    if (neededCarrots > player.getCarrotsAvailable()) {
      throw new InvalidMoveException("Nicht genug Karotten für Vorwärtszug.");
    } else {
      player.changeCarrotsAvailableBy(neededCarrots);
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
}
