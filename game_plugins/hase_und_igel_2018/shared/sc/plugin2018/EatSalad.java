package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import sc.plugin2018.util.GameRuleLogic;
import sc.shared.InvalidMoveException;

/**
 * Eine Salatessen-Aktion. Kann nur auf einem Salatfeld ausgeführt werden. Muss ausgeführt werden,
 * ein Salatfeld betreten wird. Nachdem die Aktion ausgefürht wurde, muss das Salatfeld verlassen
 * werden, oder es muss ausgesetzt werden.
 * Duch eine Salatessen-Aktion wird ein Salat verbraucht und es werden je nachdem ob der Spieler führt
 * oder nicht 10 oder 30 Karotten aufgenommen.
 */
@XStreamAlias(value = "eatSalad")
public class EatSalad extends Action {

  public EatSalad() {
    this.order = 0;
  }

  public EatSalad(int order) {
    this.order = order;
  }

  @Override
  public void perform(GameState state) throws InvalidMoveException {
    if (GameRuleLogic.isValidToEat(state)) {
      state.getCurrentPlayer().eatSalad();
      // when eating salad the carrots are increased
      if (state.getCurrentPlayer().getFieldIndex() > state.getOtherPlayer().getFieldIndex()) {
        state.getCurrentPlayer().changeCarrotsBy(10);
      } else {
        state.getCurrentPlayer().changeCarrotsBy(30);
      }
      state.setLastAction(this);
    } else {
      throw new InvalidMoveException("Es kann gerade kein Salat (mehr) gegessen werden.");
    }
  }

  @Override
  public EatSalad clone() {
    return new EatSalad(this.order);
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof EatSalad;
  }

  @Override
  public String toString() {
    return "EatSalad order " + this.order;
  }
}
