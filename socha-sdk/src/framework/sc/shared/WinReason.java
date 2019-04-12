package sc.shared;

public enum WinReason {
  SWARM("Das Spiel ist beendet.\nEin Spieler hat seinen Schwarm vereint."),
  SWARM_LARGER("Beide Spieler haben ihren Schwarm vereint. Der blaue Schwarm ist größer."),
  ROUND_LIMIT("Das Rundenlimit wurde erreicht.");

  public final String message;

  WinReason(String message) {
    this.message = message;
  }
}
