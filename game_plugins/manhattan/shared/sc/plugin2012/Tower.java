package sc.plugin2012;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
/**
 * Ein Turm an einer Position in einer Stadt.<br/><br/>
 * Auch für leere Felder nutzbar, dann hat der Turm die Hoehe 0.
 * 
 */
@XStreamAlias(value = "mh:tower")
public class Tower {

	// index des stadt in der dieser turm steht
	@XStreamAsAttribute
	public final int city;

	// index des slots auf dem dieser turm steht
	@XStreamAsAttribute
	public final int slot;

	// rote teile in diesem turm
	@XStreamAsAttribute
	private int redParts;

	// blaue teile in diesem turm
	@XStreamAsAttribute
	private int blueParts;

	// farbe des besitzers dieses turms
	@XStreamAsAttribute
	private PlayerColor owner = null;
/**
 * Ein neuer Turm an gegebener Position mit Hoehe 0
 * @param city Stadt des Turms
 * @param slot Position des Turms
 */
	public Tower(int city, int slot) {
		this.city = city;
		this.slot = slot;
	}

	/**
	 * Fuegt dem Turm ein Bauteil hinzu, wenn dies ein erlaubter Zug ist
	 * @param color Spielerfarbe des zu setzenden Bauteils
	 * @param size Groesse des zu setzenden Bauteils
	 * @return wahr, wenn der Zug erlaubt war und damit ausgefuehrt wurde
	 */
	public boolean addPart(PlayerColor color, int size) {
		if (canAddPart(color, size)) {
			owner = color;
			if (color == PlayerColor.RED) {
				redParts += size;
			} else {
				blueParts += size;
			}
			return true;
		}
		return false;
	}

	/**
	 *  gibt an, ob ein Spieler ein Bauteil auf diesen Turm setzen darf
	 * @param color Spielerfarbe des setzenden Spielers
	 * @param size Groesse des zu setzenden Bauteils
	 * @return wahr, wenn der Zug erlaubt ist.
	 */
	public boolean canAddPart(PlayerColor color, int size) {
		return color == owner || size >= Math.abs(redParts - blueParts);
	}

	/**
	 * gibt an, ob ein Spieler ein Bauteil auf diesen Turm setzen darf
	 * @param player setzender Spieler
	 * @param size Groesse des zu setzenden Bauteils
	 * @return wahr, wenn der Zug erlaubt ist
	 */
	public boolean canAddPart(Player player, int size) {
		return canAddPart(player.getPlayerColor(), size);
	}

	/**
	 * liefert den aktuellen Besitzer des Turms <br/><br/>
	 * Ist <b>null, wenn der Turm leer ist!</b>
	 * @return die Spielerfarbe des aktuellen Besitzers
	 */
	public PlayerColor getOwner() {
		return owner;
	}
	/**
	 * Gibt die Anzahl roter Segmente im Turm
	 * @return Anzahl roter Segmente
	 */
	public int getRedParts(){
		return redParts;
	}
	

	/**
	 * Gibt die Anzahl blauer Segmente im Turm
	 * @return Anzahl blauer Segmente
	 */
	public int getBlueParts(){
		return blueParts;
	}
	
	/**
	 * Gibt die Höhe des Turms, also die gesamte
	 * Anzahl Segmente
	 * @return Höhe des Turms
	 */
	public int getHeight() {
		return blueParts + redParts;
	}

	
}
