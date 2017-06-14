package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import sc.plugin2018.util.GameUtil;
import sc.shared.InvalidMoveException;

/**
 * TODO
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
    if (GameUtil.isValidToEat(state)) {
      state.getCurrentPlayer().eatSalad();
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
    if(o instanceof EatSalad) {
      return true;
    }
    return false;
  }
}
