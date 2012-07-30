package sc.plugin2013;

import sc.plugin2013.util.InvalidMoveException;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Container Klasse welche bis zu 3 Züge beherbergt
 * 
 * @author fdu
 * 
 */
@XStreamAlias(value = "cartagena:moveContainer")
public class MoveContainer implements Cloneable{
	public Move firstMove;
	public Move secondMove;
	public Move thirdMove;

	public MoveContainer() {
		this.firstMove = null;
		this.secondMove = null;
		this.thirdMove = null;
	}

	

	public MoveContainer(Move first) {
		this.firstMove = first;
		this.secondMove = null;
		this.thirdMove = null;
	}

	public MoveContainer(Move first, Move second) {
		this.firstMove = first;
		this.secondMove = second;
		this.thirdMove = null;
	}

	public MoveContainer(Move first, Move second, Move third) {
		this.firstMove = first;
		this.secondMove = second;
		this.thirdMove = third;
	}

	public void perform(GameState gameState, Player expectedPlayer)
			throws InvalidMoveException {
		if (firstMove != null) {
			firstMove.perform(gameState, expectedPlayer);
		}

		if (secondMove != null) {
			secondMove.perform(gameState, expectedPlayer);
		}

		if (thirdMove != null) {
			thirdMove.perform(gameState, expectedPlayer);
		}

	}

	/**
	 * Fügt einen Zug zum Container hinzu. Gibt die verbleibenden Slots zurück.
	 * Sollte der Container voll sein, so wird nichts geändert.
	 * 
	 * @param move
	 * @return
	 */
	public int addMove(Move move) {
		if (firstMove == null) {
			firstMove = move;
			return 2;
		} else if (secondMove == null) {
			secondMove = move;
			return 1;
		} else if (thirdMove == null) {
			thirdMove = move;
			return 0;
		} else {
			return 0;
		}
	}
	
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		MoveContainer clone = (MoveContainer) super.clone();
		if(firstMove != null)
			clone.firstMove = (Move) firstMove.clone();
		if(secondMove != null)
			clone.secondMove = (Move) secondMove.clone();
		if(thirdMove != null)
			clone.thirdMove = (Move) thirdMove.clone();
		return clone;
	}

	public boolean equals(Object obj) {
		if (obj.getClass().equals(MoveContainer.class)) {
			MoveContainer mC = (MoveContainer) obj;
			if ((mC.firstMove.equals(this.firstMove) || (mC.firstMove == null && this.firstMove == null))
					&& (mC.secondMove.equals(this.secondMove) || (mC.secondMove == null && this.secondMove == null))
					&& (mC.thirdMove.equals(this.thirdMove))
					|| (mC.thirdMove == null && this.thirdMove == null)) {
				return true;
			}
		}
		return false;

	}
}
