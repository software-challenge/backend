package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.shared.InvalidMoveException;

/**
 * TODO
 */
@XStreamAlias(value = "ExchangeCarrots")
public class ExchangeCarrots extends Action {

  @XStreamAsAttribute
  private int value;

  public ExchangeCarrots(int value, int order) {
    this.order = order;
    this.value = value;
  }



  @Override
  public void perform(GameState state, Player player) throws InvalidMoveException {

  }
}
