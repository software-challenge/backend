package kiModell.Raters;

import java.util.LinkedList;

import kiModell.Rating;

import sc.plugin2013.BackwardMove;
import sc.plugin2013.ForwardMove;
import sc.plugin2013.GameState;
import sc.plugin2013.Move;
import sc.plugin2013.MoveContainer;
import sc.plugin2013.util.InvalidMoveException;

public class RaterCombined {

	public static int getRating(GameState gameStateBefore, MoveContainer move) {
		int value = 0;

		LinkedList<Move> moveList = new LinkedList<Move>();
		if (move.firstMove != null) {
			moveList.add(move.firstMove);
		}
		if (move.secondMove != null) {
			moveList.add(move.secondMove);
		}
		if (move.thirdMove != null) {
			moveList.add(move.thirdMove);
		}
		try {
			for (Move m : moveList) {
				if (m.getClass().equals(BackwardMove.class)) {
					int cards = gameStateBefore.getCurrentPlayer().getCards()
							.size();
					m.perform(gameStateBefore,
							gameStateBefore.getCurrentPlayer());
					int newCards = gameStateBefore.getCurrentPlayer()
							.getCards().size();
					if (cards < 4) {
						if (newCards - cards == 2) {
							value += Rating.HIGH;
						}
						int startIndex = m.fieldIndex;
						int endIndex = gameStateBefore.getBoard()
								.getPreviousField(m.fieldIndex);
						int segmentsCrossed = (int) (Math.ceil(endIndex / 6) - Math
								.ceil(startIndex / 6));

						// wird ein Pirat vom Zielfeld zurückgeführt wird das
						// bestraft
						if (startIndex == 31)
							value -= Rating.VERYHIGH;

						// ein backwardzug der einen piraten weit zurück fallen
						// lässt wird bestraft
						switch (segmentsCrossed) {
						case 0:
							value += Rating.MEDIUM;
							break;
						case 1:
							value += Rating.LOW;
							break;
						case 2:
							value -= Rating.MEDIUM;
							break;
						case 3:
							value -= Rating.HIGH;
							break;
						case 4:
							value -= Rating.VERYHIGH;
							break;
						case 5:
							value -= Rating.VERYHIGH * 2;
						case 6:
							value -= Rating.VERYHIGH * 3;
						}
					} else {
						value -= Rating.HIGH;
					}
				} else {
					int startIndex = m.fieldIndex;
					ForwardMove fm = (ForwardMove) m;
					int endIndex = gameStateBefore.getBoard().getNextField(
							m.fieldIndex, fm.symbol);
					int segmentsCrossed = (int) (Math.ceil(endIndex / 6) - Math
							.ceil(startIndex / 6));

					// wenn ein zug ins Zielfeld führt ist das super
					if (endIndex == 31) {
						value += Rating.HIGH;
					}
					
					if(startIndex == getLowestIndex(gameStateBefore)){
						//damit piraten am anfang des Spielbrettes nicht "verkümmern"
						value += Rating.MEDIUM;
					}

					// ein zug, der einen Piraten weit nach vorne bringt ist
					// toll
					switch (segmentsCrossed) {
					case 0:
						value += Rating.VERYLOW;
						break;
					case 1:
						value += Rating.LOW;
						break;
					case 2:
						value += Rating.MEDIUM;
						break;
					case 3:
						value += Rating.HIGH;
						break;
					case 4:
						value += Rating.VERYHIGH;
						break;
					case 5:
						value += Rating.VERYHIGH * 2;
						break;
					case 6:
						value += Rating.VERYHIGH * 3;
						break;
					}
					fm.perform(gameStateBefore,
							gameStateBefore.getCurrentPlayer());
				}
			}
		} catch (InvalidMoveException e) {
			e.printStackTrace();
		}
		if (gameStateBefore.getCurrentPlayer().getPoints() == 36)
			return Rating.WIN;

		return value;
	}

	private static int getLowestIndex(GameState gs) {
		int value = 0;
		for(int i = 0; i< 31; i++){
			if(gs.getBoard().hasPirates(i, gs.getCurrentPlayerColor())){
				value = i;
				break;
			}			
		}
		
		return value;
	}

}
