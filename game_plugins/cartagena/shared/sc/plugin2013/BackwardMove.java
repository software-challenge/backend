package sc.plugin2013;

import sc.plugin2013.util.InvalidMoveException;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value = "cartagena:backwardMove")
public class BackwardMove extends Move{
	public final int fieldIndex;
	
	public BackwardMove(int index){
		this.fieldIndex = index;
	}

	@Override
	public void perform(GameState state, Player player) throws InvalidMoveException {
		Board board = state.getBoard();
		if(!board.hasPirates(this.fieldIndex, player.getPlayerColor())){
			throw new InvalidMoveException("Spieler hat keinen Piraten auf Feld" + fieldIndex);
		}
		if(fieldIndex == 0){
			throw new InvalidMoveException("Es ist nicht möglich einen Piraten vom Startfeld zurückzubewegen");
		}
		int nextField = board.getPreviousField(fieldIndex);
		int numPirates = board.getPirates(nextField).size();
		board.movePirate(fieldIndex, nextField, player.getPlayerColor());
		if(numPirates == 1){
			//nur eine Karte nachziehen
			player.addCard(state.drawCard());
		} else{
			//2 karten nachziehen
			player.addCard(state.drawCard());
			player.addCard(state.drawCard());
		}
	}
}
