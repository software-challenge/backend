package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.plugin2025.GameRuleLogic;
import sc.shared.InvalidMoveException;

/**
 * Karottentauschaktion. Es können auf einem Karottenfeld 10 Karotten abgegeben oder aufgenommen werden.
 * Dies kann beliebig oft hintereinander ausgeführt werden.
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

  public int getValue() {
    return value;
  }

  @Override
  public void perform(GameState state) throws InvalidMoveException {
    if (GameRuleLogic.isValidToExchangeCarrots(state, this.getValue())) {
      state.getCurrentPlayer().changeCarrotsBy(this.getValue());
      state.setLastAction(this);
    } else {
      throw new InvalidMoveException("Es können nicht " + this.getValue() + " Karotten aufgenommen werden.");
    }
  }

  @Override
  public ExchangeCarrots clone() {
    return new ExchangeCarrots(this.getValue(), this.order);
  }

  @Override
  public boolean equals(Object o) {
    if(o instanceof ExchangeCarrots) {
      return (this.getValue() == ((ExchangeCarrots) o).getValue());
    }
    return false;
  }

  @Override
  public String toString() {
    return "ExchangeCarrots value " + this.getValue() + " order " + this.order;
  }

}
