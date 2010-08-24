package sc.plugin_schaefchen;

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * ein spielzug
 * 
 * @author sca, tkra
 */
@XStreamAlias(value = "sit:move")
public final class Move {

	// das betroffene schaf
	@XStreamAsAttribute
	public final int sheep;

	// das spielfeld auf das das betroffene schaf ziehen soll
	@XStreamAsAttribute
	public final int target;

	@XStreamImplicit(itemFieldName = "hint")
	private List<DebugHint> hints;

	public Move(int sheep, int target) {
		this.sheep = sheep;
		this.target = target;
	}

	public void addHint(DebugHint hint) {
		if (hints == null) {
			hints = new LinkedList<DebugHint>();
		}

		hints.add(hint);
	}

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

}
