package sc.plugin2014.entities;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.Segment;
import sc.framework.plugins.SimplePlayer;
import com.thoughtworks.xstream.annotations.*;

/**
 * Ein Spieler, identifiziert durch seine Spielerfarbe.<br/>
 * Beeinhaltet auch Informationen zum Punktekonto, zu den {@link Segment
 * Bausteinen} und zu den {@link Card Spielkarten} des Spielers.
 * 
 */
@XStreamAlias(value = "qw:player")
public class Player extends SimplePlayer implements Cloneable {

    // spielerfarbe des spielers
    @XStreamOmitField
    private PlayerColor       color;

    // aktuelle punktzahl des spielers
    @XStreamAsAttribute
    private int               points;

    // liste der karten des spielers
    @XStreamImplicit(itemFieldName = "stone")
    private final List<Stone> stones;

    /**
     * XStream benötigt eventuell einen parameterlosen Konstruktor
     * bei der Deserialisierung von Objekten aus XML-Nachrichten.
     */
    public Player() {
        stones = null;
    }

    /**
     * einen neuen Spieler erstellen und ihm eine Spielerfarbe zuweisen
     * 
     * @param color
     *            seine Spielerfarbe
     */
    public Player(final PlayerColor color) {
        stones = new ArrayList<Stone>();
        this.color = color;
        points = 0;
    }

    /**
     * klont dieses Objekt
     * 
     * @return ein neues Objekt mit gleichen Eigenschaften
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Player clone = new Player(color);
        clone.points = points;
        if (stones != null) {
            for (Stone s : stones) {
                clone.addStone((Stone) s.clone());
            }
        }
        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Player) && (((Player) obj).color == color);
    }

    /**
     * liefert die Spielerfarbe dieses Spielers
     */
    public PlayerColor getPlayerColor() {
        return color;
    }

    /**
     * fuegt diesem Spieler eine Spielkarte hinzu
     */
    public void addStone(Stone stone) {
        stones.add(stone);
    }

    /**
     * entfernt einen Stein von diesem Spieler
     */
    public void removeStone(Stone stone) {
        stones.remove(stone);
    }

    /**
     * Prüft, ob der Spieler eine Karte für eine Position hat
     * 
     * @param slot
     *            fragliche Position
     * @return wahr, wenn Karte vorhanden
     */
    public boolean hasStone(Stone stone) {
        for (Stone s : stones) {
            if (s == stone) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @return eine Liste der Karten des Spielers
     */
    public List<Stone> getStones() {
        return stones;
    }

    /**
     * Fügt dem Punktekonto des Spielers Punkte hinzu
     * 
     * @param points
     *            Anzahl hinzuzufügender Punkte
     */
    public void addPoints(int points) {
        this.points += points;
    }

    /**
     * Liefert den Stand des Punktekontos des Spielers
     * 
     * @return Punkte des Spielers
     */
    public int getPoints() {
        return points;
    }

}
