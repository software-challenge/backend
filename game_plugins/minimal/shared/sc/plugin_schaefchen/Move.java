package sc.plugin_schaefchen;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * ein spielzug
 * 
 * @author sca, tkra
 */
@XStreamAlias(value = "sit:move")
public final class Move {

	// das betroffene schaf
	public final int sheep;

	// das spielfeld auf das das betroffene schaf ziehen soll
	public final int target;

	public Move(int sheep, int target) {
		this.sheep = sheep;
		this.target = target;
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
