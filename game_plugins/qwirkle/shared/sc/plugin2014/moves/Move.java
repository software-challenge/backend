package sc.plugin2014.moves;

import java.util.LinkedList;
import java.util.List;
import sc.plugin2014.GameState;
import sc.plugin2014.entities.Player;
import sc.plugin2014.exceptions.InvalidMoveException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Ein allgemeiner Spielzug. Dies kann ein Bau- oder ein Auswahlzug sein.
 * 
 * @see SelectMove
 * @see LayMove
 */
@XStreamAlias(value = "qw:move")
public abstract class Move implements Cloneable {

    @XStreamImplicit(itemFieldName = "hint")
    private List<DebugHint> hints;

    @Override
    public Object clone() throws CloneNotSupportedException {
        Move clone = (Move) super.clone();
        if (hints != null) {
            clone.hints = new LinkedList<DebugHint>(hints);
        }
        return clone;
    }

    /**
     * Fuegt eine Debug-Hilfestellung hinzu.<br/>
     * Diese kann waehrend des Spieles vom Programmierer gelesen werden,
     * wenn der Client einen Zug macht.
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
     * Diese kann waehrend des Spieles vom Programmierer gelesen werden,
     * wenn der Client einen Zug macht.
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
     * Diese kann waehrend des Spieles vom Programmierer gelesen werden,
     * wenn der Client einen Zug macht.
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
     * Fuehrt diesen Zug auf den uebergebenen Spielstatus aus, mit
     * uebergebenem Spieler.
     * 
     * @param state
     *            Spielstatus
     * @param player
     *            ausfuehrender Spieler
     * @throws InvalidMoveException
     *             geworfen, wenn der Zug ungueltig ist,
     *             also nicht ausfuehrbar
     */
    public abstract void perform(GameState state, Player player)
            throws InvalidMoveException;

}
