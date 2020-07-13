package sc.shared;

import sc.api.plugins.ITeam;

public class WinCondition implements Cloneable {

  /** Farbe des Gewinners */
  private final ITeam winner;

  /** Sieggrund */
  private final WinReason reason;

  public ITeam getWinner() {
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
  public WinCondition(ITeam winner, WinReason reason) {
    this.winner = winner;
    this.reason = reason;
  }

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

  public String toString(String playerName) {
    return getReason().getMessage(String.format("%s (%s)", playerName, getWinner().getDisplayName()));
  }

  @Override
  public String toString() {
    return getReason().getMessage(getWinner() != null ? getWinner().getDisplayName() : null);
  }

}
