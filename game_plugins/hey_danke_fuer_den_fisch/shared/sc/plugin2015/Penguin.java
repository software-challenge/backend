package sc.plugin2015;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Repräsentiert einen Pinguin, besitzt Information darüber, welchem Spieler sie
 * gehört
 * 
 * @author soed, nre
 * 
 */
@XStreamAlias(value = "penguin")
public class Penguin implements Cloneable {

	/**
	 * Der Besitzer der Spielfigur
	 */
	@XStreamAsAttribute
	private final PlayerColor owner;

	/**
	 * erzeugt einen Pinguin ohne Besitzer. Das sollte nur für den Server
	 * relevant sein.
	 */
	public Penguin() {
		this.owner = null;
	}

	/**
	 * Erzeugt einen Pinguin zu gegebener Spielerfarbe.
	 * 
	 * @param color
	 *            Spielerfarbe
	 */
	public Penguin(PlayerColor color) {
		this.owner = color;
	}

	/**
	 * Gibt die Farbe des zugehörigen Spielers zurück
	 * 
	 * @return Farbe des Besitzers
	 */
	public PlayerColor getOwner() {
		return this.owner;
	}

	/**
	 * Erzeugt eine deep copy dieses Pinguins.
	 * 
	 * @see java.lang.Object#clone()
	 * 
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		Penguin clone = new Penguin(this.owner);
		return clone;
	}
	public boolean equals (Object o) {
	if (o instanceof Penguin && (o == null && this == null))
		return true;
	else if (o instanceof Penguin && o != null && this != null
			&& ((Penguin) o).getOwner() == this.getOwner()) {
			return true;
	}
	return false;
	}

}
