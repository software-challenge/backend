package sc.plugin2015;

import sc.plugin2015.util.Constants;
import sc.plugin2015.util.InvalidMoveException;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Ein Laufzug. Dieser beinhaltet Informationen, von wo ein Pinguin des Spielers
 * wohin gezogen werden soll. Die Informationen sind als x- und y-Koordinaten
 * gegeben.
 * 
 */
@XStreamAlias(value = "RunMove")
public class RunMove extends Move implements Cloneable {
	
	@XStreamAsAttribute
	public final int fromX;

	@XStreamAsAttribute
	public final int fromY;

	@XStreamAsAttribute
	public final int toX;

	@XStreamAsAttribute
	public final int toY;

	/**
	 * XStream benötigt eventuell einen parameterlosen Konstruktor bei der
	 * Deserialisierung von Objekten aus XML-Nachrichten.
	 */
	public RunMove() {
		this.fromX = -1;
		this.fromY = -1;
		this.toX = -1;
		this.toY = -1;
	}

	/**
	 * 
	 * Erzeugt einen neuen Laufzug mit Anfangs- und Endkoordinaten
	 * 
	 * @param fromX
	 *            x-Koordinate des Pinguins
	 * @param fromY
	 *            y-Koordinate des Pinguins
	 * @param toX
	 *            x-Koordinate des Zielfeldes
	 * @param toY
	 *            y-Koordinate des Zielfeldes
	 */
	public RunMove(int fromX, int fromY, int toX, int toY) {
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;
	}

	/**
	 * klont dieses Objekt
	 * 
	 * @return ein neues Objekt mit gleichen Eigenschaften
	 * @throws CloneNotSupportedException
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return new RunMove(this.fromX, this.fromY, this.toX, this.toY);
	}

	@Override
	public void perform(GameState state, Player player) throws InvalidMoveException {
		if (this != null) {
			if (this.fromX < Constants.COLUMNS && this.fromY < Constants.ROWS
					&& fromX >= 0 && fromY >= 0) {
				if (state.getBoard().hasPinguin(this.fromX, this.fromY,
						player.getPlayerColor())) {
					if (state.getPossibleMoves().contains(this)) {
						player.addField();
						player.addPoints(state.getBoard().getFishNumber(
								this.fromX, this.fromY));
						state.getBoard().movePenguin(this.fromX, this.fromY,
								this.toX, this.toY, player.getPlayerColor());
					} else {
						throw new InvalidMoveException(
								"Der Zug ist nicht möglich, denn es stehen Pinguine im Weg, ein Plättchen fehlt oder der Zug ist einfach nicht möglich.");
					}
				} else {
					throw new InvalidMoveException(
							"Kein Pinguin der eigenen Farbe auf dem Startfeld vorhanden.");
				}
			} else {
				throw new InvalidMoveException(
						"Startkoordinaten sind nicht innerhalb des Spielfeldes.");
			}
		}
	}

	@Override
	public MoveType getMoveType() {
		return MoveType.RUN;
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof RunMove && ((RunMove) o).fromX == this.fromX
				&& ((RunMove) o).fromY == this.fromY
				&& ((RunMove) o).toX == this.toX && ((RunMove) o).toY == this.toY);
	}

	/**
	 * gibt ein int-Array zurück, bei dem das erste Element für die
	 * Start-x-Koordinate steht, das zweite für die Start-y-Koordinate, das
	 * dritte für die Ziel-x-Koordinate und das letzte für die
	 * Ziel-y-Koordinate.
	 * 
	 * @return Array von Koordinaten
	 */
	public int[] getRunCoordinates() {
		return new int[] { this.fromX, this.fromY, this.toX, this.toY };
	}

	/**
	 * x-Koordinate Startfeld
	 */
	public int getFromX() {
		return fromX;
	}

	/**
	 * y-Koordinate Startfeld
	 */
	public int getFromY() {
		return fromY;
	}

	/**
	 * x-Koordinate Zielfeld
	 */
	public int getToX() {
		return toX;
	}

	/**
	 * y-Koordinate Zielfeld
	 */
	public int getToY() {
		return toY;
	}

}
