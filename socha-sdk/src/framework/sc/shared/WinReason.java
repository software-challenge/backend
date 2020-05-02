package sc.shared;

public enum WinReason {
  BEE_SURROUNDED("Das Spiel ist beendet.\n%s hat die gegnerische Biene umzingelt."),
  BEE_FREE_FIELDS("Das Spiel ist beendet.\n%s hat mehr freie Felder um seine Biene."),
  ROUND_LIMIT_FREE_FIELDS("Das Rundenlimit wurde erreicht.\n%s hat mehr freie Felder um seine Biene."),
  ROUND_LIMIT_EQUAL("Das Rundenlimit wurde erreicht.\nBeide Bienen haben gleich viele freie Felder.");

  private final String message;

  WinReason(String message) {
    this.message = message;
  }

  public String getMessage(String playerName) {
    return String.format(message, playerName);
  }
}
