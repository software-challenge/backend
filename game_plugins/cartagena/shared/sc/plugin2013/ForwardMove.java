package sc.plugin2013;

import java.util.LinkedList;

import sc.plugin2013.util.InvalidMoveException;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value = "cartagena:forwardMove")
public class ForwardMove extends Move {
	@XStreamAsAttribute
	public final int fieldIndex;
	@XStreamAsAttribute
	public final SymbolType symbol;

	public ForwardMove(int index, SymbolType sym) {
		this.fieldIndex = index;
		this.symbol = sym;
	}

	@Override
	public void perform(GameState state, Player player)
			throws InvalidMoveException {
		// Invalider Move, wenn:
		// keine Piraten des Spielers an Position index
		// Spieler keine Karte des Typs Symbol hat.
		Board board = state.getBoard();
		if (!player.hasCard(symbol)) {
			throw new InvalidMoveException("Spieler hat keine Karte mit Symbol"
					+ symbol);
		}
		if (board.hasPirates(this.fieldIndex, player.getPlayerColor()) == false) {
			throw new InvalidMoveException("Spieler " + player.getPlayerColor()
					+ " hat keinen Piraten auf Feld " + fieldIndex);
		}
		// sonst nächstes freies Feld suchen und Piraten vorwärts bewegen
		int nextField = board.getNextField(fieldIndex, symbol);
		board.movePirate(fieldIndex, nextField, player.getPlayerColor());
		Card usedCard = player.removeCard(symbol);
		state.addUsedCard(usedCard);
	}

	public boolean equals(Object obj) {
		if (obj.getClass().equals(ForwardMove.class)) {
			ForwardMove fW = (ForwardMove) obj;
			if (this.fieldIndex == fW.fieldIndex
					&& this.symbol.equals(fW.symbol)) {
				return true;
			}
		}
		return false;
	}

}
