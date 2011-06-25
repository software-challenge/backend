package sc.plugin2012;

import java.util.LinkedList;
import java.util.List;

import sc.plugin2012.util.InvalideMoveException;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * ein spielzug besteht aus informationen darueber, welches schaf auf welches
 * feld ziehen soll. er kann mit debughinweisen angereichert werden, die auf der
 * grafischen oberflaeche angezeigt werden, wenn der spielzustand nach diesem
 * zug angezeigt wird.
 * 
 * @author sca, tkra
 */
@XStreamAlias(value = "mh:move")
public abstract class Move implements Cloneable {

	@XStreamImplicit(itemFieldName = "hint")
	private List<DebugHint> hints;

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
	 * einen debughinweis hinzufuegen
	 */
	public void addHint(String key, String value) {
		addHint(new DebugHint(key, value));
	}

	/**
	 * einen debughinweis hinzufuegen
	 */
	public void addHint(String string) {
		addHint(new DebugHint(string));
	}

	/**
	 * die liste der hinzugefuegten debughinweise
	 */
	public List<DebugHint> getHints() {
		return hints == null ? new LinkedList<DebugHint>() : hints;
	}

	/**
	 * diesen zug ausfuehren
	 * 
	 * @return ob der zug gueltig war und ausgefuehrt wurde
	 */
	abstract void perform(GameState state, Player player) throws InvalideMoveException;

	public abstract MoveType getMoveType();

}
