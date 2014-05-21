package sc.plugin2015;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import sc.plugin2015.util.InvalidMoveException;

@XStreamAlias(value = "NullMove")
public class NullMove extends Move implements Cloneable {

	/**
	 * erzeugt einen neuen Aussetzzug.
	 */
	public NullMove() {

	}

	@Override
	void perform(GameState state, Player player) throws InvalidMoveException {
		// Ein NullMove macht eben nichts.
	}

	@Override
	public MoveType getMoveType() {
		return MoveType.RUN;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return new NullMove();
	}
	

	@Override
	public boolean equals(Object o) {
		return (o instanceof NullMove);
	}

}
