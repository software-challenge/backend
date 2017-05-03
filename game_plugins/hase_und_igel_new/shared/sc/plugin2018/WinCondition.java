package sc.plugin2018;

/**
 * Beinhaltet Informationen zum Spielende: Farbe des Gewinners und Gewinngrund.
 *
 */
public class WinCondition implements Cloneable {

	/**
	 * Farbe des Gewinners
	 */
	private final PlayerColor winner;

	/**
	 * Sieggrund
	 */
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
	 * @throws CloneNotSupportedException falls Objekt nicht geklont werden kann
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return new WinCondition(winner, reason);
	}

}
