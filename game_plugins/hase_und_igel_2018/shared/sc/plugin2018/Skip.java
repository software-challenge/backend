package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import sc.plugin2018.util.GameRuleLogic;
import sc.shared.InvalidMoveException;

/**
 * Ein Aussetzzug. Ist nur erlaubt, sollten keine anderen Züge möglich sein.
 */
@XStreamAlias(value = "skip")
public class Skip extends Action {

  /**
   * Konstruktor für einen Aussetzzug. Ein Aussetzzug sollte immer die einzige und erste Aktion eines
   * Zuges sein.
   */
  public Skip() {
    this.order = 0;
  }

  public Skip(int order) {
    this.order = 0;
  }

  public void perform(GameState state) throws InvalidMoveException {
    // this methods does literally nothing
    if(this.order > 0) {
      throw new InvalidMoveException("Nur das ausspielen von Karten ist nach der ersten Aktion erlaubt.");
    }
    if (!GameRuleLogic.isValidToSkip(state)) {
      throw new InvalidMoveException("Spieler kann noch einen anderen Zug ausführen, aussetzen ist nicht erlaubt.");
    }
  }

  @Override
  public Skip clone() {
    return new Skip(this.order);
  }

  @Override
  public boolean equals(Object o) {
    if(o instanceof Skip) {
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return "Skip order " + this.order;
  }
}
