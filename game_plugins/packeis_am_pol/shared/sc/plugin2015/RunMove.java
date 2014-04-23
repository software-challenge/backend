package sc.plugin2015;

import sc.plugin2015.util.Constants;
import sc.plugin2015.util.InvalideMoveException;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 * Ein Bauzug. Dieser beinhaltet Informationen, welcher Baustein wohin gesetzt
 * wird.
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
	 *           x-Koordinate des Pinguins
	 * @param fromY
	 *           y-Koordinate des Pinguins
	 * @param toX
	 *           x-Koordinate des Zielfeldes
	 * @param toY
	 *           y-Koordinate des Zielfeldes
	 */
	public RunMove(int fromX, int fromY, int toX, int toY) {
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;
	}
         /**
         * klont dieses Objekt
         * @return ein neues Objekt mit gleichen Eigenschaften
         * @throws CloneNotSupportedException 
         */
        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

	@Override
	void perform(GameState state, Player player) throws InvalideMoveException {
		if(this != null) {
			if(this.fromX < Constants.COLUMNS && this.fromY < Constants.ROWS) {
				if(state.getBoard().hasPinguin(this.fromX, this.fromY, player.getPlayerColor())) {
					if(state.getPossibleMoves().contains(this)) {
						player.addField();
						player.addPoints(state.getBoard().getFish(this.fromX, this.fromY));
						state.getBoard().movePenguin(this.fromX, this.fromY, this.toX, this.toY, player.getPlayerColor());
					} else {/*
						for(int i = 0; i < state.getPossibleMoves().size(); i++) {
							System.out.print("fromX=" + state.getPossibleMoves().get(i).fromX);
							System.out.print("fromY=" + state.getPossibleMoves().get(i).fromY);
							System.out.print("toX=" + state.getPossibleMoves().get(i).toX);
							System.out.println("toY=" + state.getPossibleMoves().get(i).toY);
						}*/
						throw new InvalideMoveException("Der Zug ist nicht möglich, es stehen Pinguine im Weg, ein Plättchen fehlt oder der Zug ist einfach nicht möglich.");
					}
				} else {
					throw new InvalideMoveException("Kein Pinguin der eigenen Farbe auf dem Startfeld vorhanden.");
				}
			} else {
				throw new InvalideMoveException("Startkoordinaten sind nicht innerhalb des Spielfeldes.");
			}
		}
	}

	@Override
	public MoveType getMoveType() {
		return MoveType.RUN;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof RunMove 
				&&((RunMove) o).fromX == this.fromX
				&& ((RunMove) o).fromY == this.fromY
				&& ((RunMove) o).toX == this.toX
				&& ((RunMove) o).toY == this.toY) 
			return true;
		return false;
	}

}
