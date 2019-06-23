package sc.shared;

public enum WinReason {
  SWARM_EQUAL("Beide Spieler haben einen Schwarm gleicher Größe gebildet."),
  SWARM_CONNECTED("%s hat seinen Schwarm vereint."),
  SWARM_LARGER("Beide Spieler haben ihren Schwarm vereint.\nDer Schwarm von %s ist größer."),
  ROUND_LIMIT("Das Rundenlimit wurde erreicht.");

  private final String message;

  WinReason(String message) {
    this.message = message;
  }

  public String getMessage(String playerName) {
    return String.format(message, playerName);
  }
}
