package sc.plugin2013;

import sc.plugin2013.util.Constants;
import sc.plugin2013.util.InvalidMoveException;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value = "cartagena:backwardMove")
public class BackwardMove extends Move {
	@XStreamAsAttribute
	public int fieldIndex;

	public BackwardMove(int index) {
		this.fieldIndex = index;
	}

	@Override
	public void perform(GameState state, Player player)
			throws InvalidMoveException {
		Board board = state.getBoard();
		if(this.fieldIndex < 0 || this.fieldIndex > board.size()-1){
			throw new InvalidMoveException("Ungültigen Feldindex Angegeben");
		}		
		if (!board.hasPirates(this.fieldIndex, player.getPlayerColor())) {
			throw new InvalidMoveException(
					"Spieler hat keinen Piraten auf Feld " + fieldIndex);
		}
		if (fieldIndex == 0) {
			throw new InvalidMoveException(
					"Es ist nicht möglich einen Piraten vom Startfeld zurückzubewegen");
		}
		int nextField = board.getPreviousField(fieldIndex);
		if(nextField == -1){
			throw new InvalidMoveException("Es gibt kein zurückliegendes Feld");
		}
		int numPirates = board.getPirates(nextField).size();
		board.movePirate(fieldIndex, nextField, player.getPlayerColor());
		if (numPirates == 1) {
			// nur eine Karte nachziehen
			if (player.getNumCards() < Constants.MAX_CARDS_PER_PLAYER) {
				player.addCard(state.drawCard());
			}

		} else {
			if (player.getNumCards() < Constants.MAX_CARDS_PER_PLAYER - 1) {
				// 2 karten nachziehen
				player.addCard(state.drawCard());
				player.addCard(state.drawCard());
			} else if (player.getNumCards() < Constants.MAX_CARDS_PER_PLAYER) {
				// nur eine Karte ziehen
				player.addCard(state.drawCard());
			}
		}
	}
	
	public boolean equals(Object obj) {
		if (obj.getClass().equals(BackwardMove.class)) {
			BackwardMove fW = (BackwardMove) obj;
			if (this.fieldIndex == fW.fieldIndex) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		BackwardMove clone =  (BackwardMove) super.clone();
		clone.fieldIndex = this.fieldIndex;
		return clone;
	}
}
