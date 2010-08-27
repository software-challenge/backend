package sc.plugin_schaefchen;

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * ein spielzug besteht aus informationen darueber, welches schaf auf welches
 * feld ziehen soll. er kann mit debughinweisen angereichert werden, die auf der
 * grafischen oberflaeche angezeigt werden, wenn der spielzustand nach diesem
 * zug angezeigt wird.
 * 
 * @author sca, tkra
 */
@XStreamAlias(value = "sit:move")
public final class Move implements Cloneable {

	/**
	 * der index des zu bewegenden schafes
	 */
	@XStreamAsAttribute
	public final int sheep;

	/**
	 * der index des spielfeldes, auf das sich das schaf bewegen soll
	 */
	@XStreamAsAttribute
	public final int target;

	@XStreamImplicit(itemFieldName = "hint")
	private List<DebugHint> hints;

	/**
	 * einen neuen spielzug erstellen.
	 * 
	 * @param sheep
	 *            der index des schafs das bewegt werden soll.
	 * @param target
	 *            der index des spielfeldes, auf den das schaf gesetzt werden
	 *            soll.
	 */
	public Move(int sheep, int target) {
		this.sheep = sheep;
		this.target = target;
	}

	/**
	 * einen debighinweis hinzufuegen
	 */
	public void addHint(DebugHint hint) {
		if (hints == null) {
			hints = new LinkedList<DebugHint>();
		}

		hints.add(hint);
	}

	/**
	 * die liste der hinzugefuegten debughinweise
	 */
	public List<DebugHint> getHints() {
		return hints == null ? new LinkedList<DebugHint>() : hints;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Move) {
			Move other = (Move) obj;
			return other.sheep == sheep && other.target == target;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Ziehe mit Schaf " + sheep + " auf Feld " + target;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		Move clone = new Move(sheep, target);
		for(DebugHint hint : hints){
			clone.addHint((DebugHint) hint.clone());
		}
		return clone;
	}

}
