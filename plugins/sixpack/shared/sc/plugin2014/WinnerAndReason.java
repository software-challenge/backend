package sc.plugin2014;

import sc.plugin2014.entities.PlayerColor;

/**
 * Beinhaltet Informationen zum Spielende:
 * Farbe des Gewinners und Gewinngrund.
 * 
 */
class WinnerAndReason implements Cloneable {

    public final PlayerColor winner;

    public final String      reason;

    /**
     * XStream benötigt eventuell einen parameterlosen Konstruktor
     * bei der Deserialisierung von Objekten aus XML-Nachrichten.
     */
    public WinnerAndReason() {
        winner = null;
        reason = null;
    }

    /**
     * erzeugt eine neue Condition mit Sieger und Geiwnngrund
     * 
     * @param winner
     *            Farbe des Siegers
     * @param reason
     *            TExt, der Sieg beschreibt
     */
    public WinnerAndReason(PlayerColor winner, String reason) {
        this.winner = winner;
        this.reason = reason;
    }

    /**
     * klont dieses Objekt
     * 
     * @return ein neues Objekt mit gleichen Eigenschaften
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return new WinnerAndReason(winner, reason);
    }

}
