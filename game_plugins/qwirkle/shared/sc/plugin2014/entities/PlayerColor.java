package sc.plugin2014.entities;

/**
 * Ein Spieler spielt entweder in rot (als erster Spieler) oder blau (zweiter
 * Spieler)
 * 
 */
public enum PlayerColor {

    RED, BLUE;

    /**
     * liefert die Spielerfarbe des Gegners dieses Spielers
     */
    public PlayerColor opponent() {
        return this == RED ? BLUE : RED;
    }

}
