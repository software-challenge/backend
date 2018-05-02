package sc.shared;

public enum PlayerColor {

  RED, BLUE;

  /**
   * liefert die Spielerfarbe des Gegners dieses Spielers
   * @return Spielerfarbe des Gegners
   */
  public PlayerColor opponent() {
    return this == RED ? BLUE : RED;
  }

}
