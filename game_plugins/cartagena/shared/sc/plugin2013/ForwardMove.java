package sc.plugin2013;


import sc.plugin2013.util.ForwardMoveConverter;
import sc.plugin2013.util.InvalidMoveException;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias(value = "cartagena:forwardMove")
@XStreamConverter(ForwardMoveConverter.class)
public class ForwardMove extends Move {
	@XStreamAsAttribute
	public int fieldIndex;
	@XStreamAsAttribute
	public SymbolType symbol;
	
	private volatile boolean wrongSymbolString = false;

	public ForwardMove(int index, SymbolType sym) {
		this.fieldIndex = index;
		this.symbol = sym;
	}

	@Override
	public void perform(GameState state, Player player)
			throws InvalidMoveException {
		// Invalider Move, wenn:
		// falscher Feldindex
		// keine Piraten des Spielers an Position index
		// Spieler keine Karte des Typs Symbol hat.
		Board board = state.getBoard();
		if (this.fieldIndex < 0 || this.fieldIndex > board.size() - 1) {
			throw new InvalidMoveException("Ungültigen Feldindex Angegeben");
		}
		if (this.fieldIndex == board.size() - 1) {
			throw new InvalidMoveException(
					"Vorwärtszug vom Zielfeld nicht möglich");
		}
		if (!player.hasCard(symbol)) {
			throw new InvalidMoveException(
					"Spieler hat keine Karte mit Symbol " + symbol);
		}
		if (board.hasPirates(this.fieldIndex, player.getPlayerColor()) == false) {
			throw new InvalidMoveException("Spieler " + player.getPlayerColor()
					+ " hat keinen Piraten auf Feld " + fieldIndex);
		}
		if(this.wrongSymbolString){
			throw new InvalidMoveException("Gesendetes Symbol existiert nicht");
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

	@Override
	public Object clone() throws CloneNotSupportedException {
		ForwardMove clone = (ForwardMove) super.clone();
		clone.fieldIndex = this.fieldIndex;
		clone.symbol = this.symbol;
		return clone;
	}

}
