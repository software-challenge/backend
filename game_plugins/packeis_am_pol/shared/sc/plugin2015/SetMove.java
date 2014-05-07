package sc.plugin2015;

import static sc.plugin2015.util.Constants.ROWS;
import static sc.plugin2015.util.Constants.COLUMNS;
import sc.plugin2015.util.InvalidMoveException;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 * 
 * Ein Setzzug zum setzen eines Pinguins auf ein Spielfeldplättchen.
 * 
 * @see Move
 * 
 */
@XStreamAlias(value = "SetMove")
public class SetMove extends Move implements Cloneable {

	@XStreamAsAttribute
	private int setX;

	@XStreamAsAttribute
	private int setY;

	/**
	 * XStream benötigt eventuell einen parameterlosen Konstruktor bei der
	 * Deserialisierung von Objekten aus XML-Nachrichten.
	 */
	public SetMove() {
	}

	/**
	 * Einen Setzzug anhand zweier Integer erzeugen, bei dem setX die Spalte und
	 * setY die Reihe angibt.
	 * 
	 * @param setX
	 *            Die Spalte des zu setzenden Pinguins
	 * @param setY
	 *            die Reihe des zu setzenden Pinguins
	 */
	public SetMove(int setX, int setY) {
		this.setX = setX;
		this.setY = setY;
	}

	/**
	 * klont dieses Objekt
	 * 
	 * @return ein neues Objekt mit gleichen Eigenschaften
	 * @throws CloneNotSupportedException
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		SetMove clone = (SetMove) super.clone();
		clone.setX = this.setX;
		clone.setY = this.setY;
		return clone;
	}

	/**
	 * Gibt ein int-Array zurueck, bei dem das erste Element fuer die Spalte und
	 * das zweite Element für die Reihe steht.
	 * 
	 * @return die Koordinaten des Zuges.
	 */
	public int[] getSetCoordinates() {
		return new int[] { this.setX, this.setY };
	}

	@Override
	void perform(GameState state, Player player) throws InvalidMoveException {

		if ((setY & 1) == 0) {
			if (setX < COLUMNS - 1 && setY < ROWS && setX >= 0 && setY >= 0) {
				if (state.getBoard().getFishNumber(setX, setY) == 1) {
					if (state.getBoard().getPenguin(setX, setY) == null) {
						state.getBoard()
								.getField(setX, setY)
								.putPenguin(
										new Penguin(player.getPlayerColor()));
					} else {
						throw new IllegalArgumentException(
								"Setzzug konnte nicht konstruiert werden: Feld ist schon besetzt.");
					}
				} else {
					throw new IllegalArgumentException(
							"Setzzug konnte nicht konstruiert werden: Feld hat mehr als einen Fisch.");
				}
			} else {
				throw new IllegalArgumentException(
						"Setzzug konnte nicht konstruiert werden: Nicht innerhalb des Spielfeldes");
			}
		} else {
			if (setX < COLUMNS && setY < ROWS && setX >= 0 && setY >= 0) {
				if (state.getBoard().getFishNumber(setX, setY) == 1) {
					if (state.getBoard().getPenguin(setX, setY) == null) {
						state.getBoard()
								.getField(setX, setY)
								.putPenguin(
										new Penguin(player.getPlayerColor()));
					} else {
						throw new IllegalArgumentException(
								"Setzzug konnte nicht konstruiert werden: Feld ist schon besetzt.");
					}
				} else {
					throw new IllegalArgumentException(
							"Setzzug konnte nicht konstruiert werden: Feld hat mehr als einen Fisch.");
				}
			} else {
				throw new IllegalArgumentException(
						"Setzzug konnte nicht konstruiert werden: Nicht innerhalb des Spielfeldes");
			}
		}
	}

	@Override
	public MoveType getMoveType() {
		return MoveType.SET;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof SetMove && ((SetMove) o).setX == this.setX
				&& ((SetMove) o).setY == this.setY)
			return true;
		return false;
	}
}
