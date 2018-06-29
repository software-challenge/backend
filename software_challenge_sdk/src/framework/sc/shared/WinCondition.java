package sc.shared;

public class WinCondition implements Cloneable {

  /** Farbe des Gewinners */
  private final PlayerColor winner;

  /** Sieggrund */
  private final String reason;

  public PlayerColor getWinner() {
    return winner;
  }

  public String getReason() {
    return reason;
  }

  /**
   * Erzeugt eine neue Condition mit Sieger und Gewinngrund
   *
   * @param winner
   *            Farbe des Siegers
   * @param reason
   *            Text, der Sieg beschreibt
   */
  public WinCondition(PlayerColor winner, String reason) {
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
      return this.getReason() == wq.getReason();
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return "winner: " + this.getWinner() +" reason: " + this.getReason();
  }

}
