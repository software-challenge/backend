package sc.plugin2018;

import sc.plugin2018.util.GameUtil;
import sc.shared.InvalidMoveException;

/**
 * Ein Aussetzzug. Ist nur erlaubt, sollten keine anderen Züge möglich sein.
 */
public class Skip extends Action {

  /**
   * Konstruktor für einen Aussetzzug. Ein Aussetzzug sollte immer die einzige und erste Aktion eines
   * Zuges sein.
   */
  public Skip() {
    this.order = 0;
  }

  public void perform(GameState state, Player player) throws InvalidMoveException {
    // this methods does literally nothing
    if(this.order > 0) {
      throw new InvalidMoveException("Nur das ausspielen von Karten ist nach der ersten Aktion erlaubt.");
    }
    if (!GameUtil.isValidToSkip(state, player)) {
      throw new InvalidMoveException("Spieler kann noch einen anderen Zug ausführen, aussetzen ist nicht erlaubt.");
    }
  }


}
