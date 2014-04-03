package sc.plugin2015;

import java.util.LinkedList;
import java.util.List;

import sc.framework.plugins.SimplePlayer;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Ein Spieler, identifiziert durch seine Spielerfarbe.<br/>
 * Beeinhaltet auch Informationen zum Punktekonto und der 
 * Anzahl der Plättchen des Spielers.
 * 
 */
@XStreamAlias(value = "manhattan:player")
public class Player extends SimplePlayer implements Cloneable {

	// spielerfarbe des spielers
	@XStreamOmitField
	private PlayerColor color;

	// aktuelle punktzahl des spielers
	@XStreamAsAttribute
	private int points;

	// Anzahl der gesammelten Plättchen
	@XStreamImplicit(itemFieldName = "segment")
	private final int fields;

         /**
         * XStream benötigt eventuell einen parameterlosen Konstruktor
         * bei der Deserialisierung von Objekten aus XML-Nachrichten.
         */
        public Player() {
            fields = 0;
        }
        /**
	 * einen neuen Spieler erstellen und ihm eine Spielerfarbe zuweisen
	 * 
	 * @param color
	 *            seine Spielerfarbe
	 */
	public Player(final PlayerColor color) {
		fields = 0;
		this.color = color;
		points = 0;
	}

	 /**
         * klont dieses Objekt
         * @return ein neues Objekt mit gleichen Eigenschaften
         * @throws CloneNotSupportedException 
         */
        @Override
        public Object clone() throws CloneNotSupportedException {
            Player clone = new Player(this.color);
            clone.points = this.points;
            clone.fields = this.fields;
            return clone;
        }

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Player) && ((Player) obj).color == this.color;
	}

	/**
	 * liefert die Spielerfarbe dieses Spielers
	 */
	public PlayerColor getPlayerColor() {
		return color;
	}

	/**
	 * fuegt ein Plättchen hinzu
	 */
	public void addField() {
		this.fields++;
	}
	/**
	 * Gibt eine Anzahl der Plättchen des Spielers zurück.
	 * @return Anzahl der Plättchen des Spielers
	 */
	public int getFields() {
		return fields;
	}

	/**
	 * Fügt dem Punktekonto des Spielers Punkte hinzu
	 * @param points Anzahl hinzuzufügender Punkte
	 */
	public void addPoints(int points) {
		this.points += points;
	}

	/**
	 * Liefert den Stand des Punktekontos des Spielers
	 * @return Punkte des Spielers
	 */
	public int getPoints() {
		return points;
	}
}
