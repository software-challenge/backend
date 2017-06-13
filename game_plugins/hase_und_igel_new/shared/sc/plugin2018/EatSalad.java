package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import sc.shared.InvalidMoveException;

/**
 * TODO
 */
@XStreamAlias(value = "EatSalad")
public class EatSalad extends Action {

  @Override
  public void perform(GameState state, Player player) throws InvalidMoveException {

  }
}
