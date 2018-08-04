package sc.plugin2015;

import java.util.LinkedList;
import java.util.List;

import sc.plugin2015.util.InvalidMoveException;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Ein allgemeiner Spielzug. Dies kann ein Setz-, ein Lauf- oder ein Aussetzzug
 * sein.
 * 
 * @see SetMove
 * @see RunMove
 * @see NullMove
 */
@XStreamAlias(value = "move")
public abstract class Move implements Cloneable {

	@XStreamImplicit(itemFieldName = "hint")
	private List<DebugHint> hints;

	/**
	 * klont dieses Objekt
	 * 
	 * @return ein neues Objekt mit gleichen Eigenschaften
	 * @throws CloneNotSupportedException
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		Move clone = (Move) super.clone();
		if (this.hints != null)
			clone.hints = new LinkedList<DebugHint>(this.hints);
		return clone;
	}

	/**
	 * Fuegt eine Debug-Hilfestellung hinzu.<br/>
	 * Diese kann waehrend des Spieles vom Programmierer gelesen werden, wenn
	 * der Client einen Zug macht.
	 * 
	 * @param hint
	 *            hinzuzufuegende Debug-Hilfestellung
	 */
	public void addHint(DebugHint hint) {
		if (hints == null) {
			hints = new LinkedList<DebugHint>();
		}
		hints.add(hint);
	}

	/**
	 * 
	 * Fuegt eine Debug-Hilfestellung hinzu.<br/>
	 * Diese kann waehrend des Spieles vom Programmierer gelesen werden, wenn
	 * der Client einen Zug macht.
	 * 
	 * @param key
	 *            Schluessel
	 * @param value
	 *            zugehöriger Wert
	 */
	public void addHint(String key, String value) {
		addHint(new DebugHint(key, value));
	}

	/**
	 * Fuegt eine Debug-Hilfestellung hinzu.<br/>
	 * Diese kann waehrend des Spieles vom Programmierer gelesen werden, wenn
	 * der Client einen Zug macht.
	 * 
	 * @param string
	 *            Debug-Hilfestellung
	 */
	public void addHint(String string) {
		addHint(new DebugHint(string));
	}

	/**
	 * Gibt die Liste der hinzugefuegten Debug-Hilfestellungen zurueck
	 * 
	 * @return Liste der hinzugefuegten Debug-Hilfestellungen
	 */
	public List<DebugHint> getHints() {
		return hints == null ? new LinkedList<DebugHint>() : hints;
	}

	/**
	 * Fuehrt diesen Zug auf den uebergebenen Spielstatus aus, mit uebergebenem
	 * Spieler.
	 * 
	 * @param state
	 *            Spielstatus
	 * @param player
	 *            ausfuehrender Spieler
	 * @throws InvalidMoveException
	 *             geworfen, wenn der Zug ungueltig ist, also nicht ausfuehrbar
	 */
	abstract void perform(GameState state, Player player)
			throws InvalidMoveException;

	/**
	 * Gibt die Art des Zuges zurueck
	 * 
	 * @return Zugart
	 */
	public abstract MoveType getMoveType();

}
