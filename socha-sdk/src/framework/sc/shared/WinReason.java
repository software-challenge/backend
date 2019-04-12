package sc.shared;

public enum WinReason {
  SWARM_CONNECTED("Das Spiel ist beendet.\n%s hat seinen Schwarm vereint."),
  SWARM_LARGER("Beide Spieler haben ihren Schwarm vereint.\nDer Schwarm von %s ist größer."),
  ROUND_LIMIT("Das Rundenlimit wurde erreicht.");

  private final String message;

  WinReason(String message) {
    this.message = message;
  }

  public String getMessage(PlayerColor color) {
    return String.format(message, color);
  }
}
