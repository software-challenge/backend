package kiModell;

import kiModell.Raters.RaterCombined;

import sc.plugin2013.GameState;
import sc.plugin2013.MoveContainer;

public class Node implements Comparable<Node> {
	//private GameState gameStateRaw;
	private MoveContainer moveToPerform;
	private int rating = 0;

	public Node(MoveContainer mvc) {
			//this.gameStateRaw = (GameState) gs.clone();
			this.moveToPerform = mvc;
	}

//	public void performMove() {
//		try {
//			if (!performed) {
//				moveToPerform.perform(gameStatePerf,
//						gameStatePerf.getCurrentPlayer());
//				this.performed = true;
//			}
//		} catch (InvalidMoveException e) {
//			System.out.println("Invalid Move Executed");
//			e.printStackTrace();
//		}
//	}

	public int rateMove(GameState gameStateRaw) {
			this.rating += RaterCombined.getRating(gameStateRaw,
					moveToPerform);
		return this.rating;
	}

	@Override
	public int compareTo(Node n) {
		if (this.rating > n.rating) {
			return 1;
		} else if (this.rating < n.rating) {
			return -1;
		} else {
			return 0;
		}

	}

	public MoveContainer getMoveToPerform() {
		return moveToPerform;
	}

	public void setMoveToPerform(MoveContainer moveToPerform) {
		this.moveToPerform = moveToPerform;
	}

}
