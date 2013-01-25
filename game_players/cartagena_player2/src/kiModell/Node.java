package kiModell;

import kiModell.Raters.RaterCombined;

import sc.plugin2013.GameState;
import sc.plugin2013.MoveContainer;
import sc.plugin2013.util.InvalidMoveException;

public class Node implements Comparable<Node> {
	private GameState gameStateRaw;
	private GameState gameStatePerf;
	private MoveContainer moveToPerform;
	private int rating = 0;
	private boolean performed = false;

	public Node(GameState gs, MoveContainer mvc) {
		try {
			this.gameStateRaw = (GameState) gs.clone();
			this.gameStatePerf = (GameState) gs.clone();
			this.moveToPerform = mvc;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	public void performMove() {
		try {
			if (!performed) {
				moveToPerform.perform(gameStatePerf,
						gameStatePerf.getCurrentPlayer());
				this.performed = true;
			}
		} catch (InvalidMoveException e) {
			System.out.println("Invalid Move Executed");
			e.printStackTrace();
		}
	}

	public int rateMove() {

		try {
			this.rating += RaterCombined.getRating((GameState) gameStateRaw.clone(),
					moveToPerform);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

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
