package sc.shared;

public class WinCondition implements Cloneable {

  /** Farbe des Gewinners */
  private final PlayerColor winner;

  /** Sieggrund */
  private final WinReason reason;

  public PlayerColor getWinner() {
    return winner;
  }

  public WinReason getReason() {
    return reason;
  }

  /**
   * Erzeugt eine neue Condition mit Sieger und Gewinngrund
   *
   * @param winner Farbe des Siegers
   * @param reason Text, der Sieg beschreibt
   */
  public WinCondition(PlayerColor winner, WinReason reason) {
    this.winner = winner;
    this.reason = reason;
  }

  /**
   * klont dieses Objekt
   *
   * @return ein neues Objekt mit gleichen Eigenschaften
   */
  @Override
  public Object clone() {
    return new WinCondition(winner, reason);
  }

  public boolean equals(Object eq) {
    if (eq instanceof WinCondition) {
      WinCondition wq = (WinCondition) eq;
      if (this.getWinner() != wq.getWinner()) {
        return false;
      }
      return this.getReason().equals(wq.getReason());
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return getReason().getMessage(getWinner());
  }

}
