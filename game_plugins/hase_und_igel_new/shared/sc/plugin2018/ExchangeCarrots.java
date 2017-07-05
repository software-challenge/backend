package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.plugin2018.util.GameRuleLogic;
import sc.shared.InvalidMoveException;

/**
 * TODO
 */
@XStreamAlias(value = "exchangeCarrots")
public class ExchangeCarrots extends Action {

  @XStreamAsAttribute
  private int value;

  public ExchangeCarrots(int value) {
    this.order = 0;
    this.value = value;
  }

  public ExchangeCarrots(int value, int order) {
    this.order = order;
    this.value = value;
  }



  @Override
  public void perform(GameState state) throws InvalidMoveException {
    if (GameRuleLogic.isValidToExchangeCarrots(state, this.value)) {
      state.getCurrentPlayer().changeCarrotsAvailableBy(this.value);
    } else {
      throw new InvalidMoveException("Es können nicht " + this.value + " Karotten aufgenommen werden.");
    }
  }

  @Override
  public ExchangeCarrots clone() {
    return new ExchangeCarrots(this.value, this.order);
  }

  @Override
  public boolean equals(Object o) {
    if(o instanceof ExchangeCarrots) {
      return (this.value == ((ExchangeCarrots) o).value);
    }
    return false;
  }

}
