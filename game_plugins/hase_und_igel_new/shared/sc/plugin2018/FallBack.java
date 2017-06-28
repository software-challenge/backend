package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import sc.plugin2018.util.GameRuleLogic;
import sc.shared.InvalidMoveException;

/**
 * TODO
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
      state.getCurrentPlayer().changeCarrotsAvailableBy(10 * (previousFieldIndex - state.getCurrentPlayer().getFieldIndex()));
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
}
