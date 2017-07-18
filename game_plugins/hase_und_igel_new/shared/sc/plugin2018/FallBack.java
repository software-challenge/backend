package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import sc.plugin2018.util.GameRuleLogic;
import sc.shared.InvalidMoveException;

/**
 * Rückzugaktion. Sollte das nächste Igelfeld hinter einem Spieler nicht belegt sein, darf anstatt nach
 * vorne zu ziehen ein Rückzug gemacht werden. Dabei werden die zurückgezogene Distanz * 10 Karotten aufgenommen.
 */
@XStreamAlias(value = "fallBack")
public class FallBack extends Action {

  public FallBack() {
    this.order = 0;
  }

  public FallBack(int order) {
    this.order = order;
  }

  @Override
  public void perform(GameState state) throws InvalidMoveException {
    if (GameRuleLogic.isValidToFallBack(state)) {
      int previousFieldIndex = state.getCurrentPlayer().getFieldIndex();
      state.getCurrentPlayer().setFieldIndex(state.getPreviousFieldByType(FieldType.HEDGEHOG, state.getCurrentPlayer()
              .getFieldIndex()));
      state.getCurrentPlayer().changeCarrotsBy(10 * (previousFieldIndex - state.getCurrentPlayer().getFieldIndex()));
      state.setLastAction(this);
    } else {
      throw new InvalidMoveException("Es kann gerade kein Rückzug gemacht werden.");
    }
  }

  @Override
  public FallBack clone() {
    return new FallBack(this.order);
  }

  @Override
  public boolean equals(Object o) {
    if(o instanceof FallBack) {
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return "FallBack order " + this.order;
  }
}
