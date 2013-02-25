package kiModell;

import java.util.LinkedList;
import java.util.List;

import sc.plugin2013.GameState;
import sc.plugin2013.Move;
import sc.plugin2013.MoveContainer;
import sc.plugin2013.util.InvalidMoveException;

/**
 * @author fdu Tries out every possible Move for the current player (Looks Ahead
 *         one Round). Picks the best one.
 */
public class MoveRater {
	private GameState startGameState;
	private List<Node> possibleMoveNodes;
	private int MAXTIME = 2000;
	private double time;

	public MoveRater() {
		possibleMoveNodes = new LinkedList<Node>();
	}

	public MoveContainer getBestNextMove(GameState gs) {
		System.gc();
		this.time = System.currentTimeMillis();
		try {
			this.startGameState = (GameState) gs.clone();
			this.possibleMoveNodes = new LinkedList<Node>();
		} catch (CloneNotSupportedException e1) {
			e1.printStackTrace();
		}
		getPossibleMoveContainers();

		System.out.println("NodeBuilding took "
				+ (System.currentTimeMillis() - this.time) + " ms to finish");
		System.out.println("Number of Nodes: " + possibleMoveNodes.size());
		int bestRating = -Rating.WIN;
		Node bestNode = null;
		for (Node n : possibleMoveNodes) {
			int rating;
			try {
				rating = n.rateMove((GameState) this.startGameState.clone());
				if (rating > bestRating) {
					bestRating = rating;
					bestNode = n;
				}
				if (System.currentTimeMillis() - time > this.MAXTIME) {
					break;
				}
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}

		}

		if (bestNode == null) {
			bestNode = new Node(new MoveContainer());
		}
		System.out.println("It took me "
				+ (System.currentTimeMillis() - this.time) + " ms to finish");
		System.out.println("Number of Nodes: " + possibleMoveNodes.size());
		return bestNode.getMoveToPerform();
	}

	public void getPossibleMoveContainers() {
		List<Move> list = this.startGameState.getPossibleMoves();
		LinkedList<MoveContainer> stageOneList = new LinkedList<MoveContainer>();
		LinkedList<MoveContainer> stageTwoList = new LinkedList<MoveContainer>();
		// LinkedList<MoveContainer> stageThreeList = new
		// LinkedList<MoveContainer>();
		// Move Container mit nur einem Move
		for (Move m : list) {
			if (System.currentTimeMillis() - time > this.MAXTIME) {
				return;
			}
			MoveContainer mc = new MoveContainer(m);
			stageOneList.add(mc);

			Node node = new Node(mc);
			possibleMoveNodes.add(node);
		}
		// Liste mit 2 Teilzügen erstellen
		for (MoveContainer mc : stageOneList) {
			try {
				if (System.currentTimeMillis() - time > this.MAXTIME) {
					return;
				}
				MoveContainer mcCopy = cloneMoveContainer(mc);
				GameState gs = (GameState) this.startGameState.clone();
				mcCopy.firstMove.perform(gs, gs.getCurrentPlayer());
				for (Move m : gs.getPossibleMoves()) {
					if (System.currentTimeMillis() - time > this.MAXTIME) {
						return;
					}
					MoveContainer container = cloneMoveContainer(mcCopy);
					container.addMove(m);
					stageTwoList.add(container);
					Node node = new Node(container);
					possibleMoveNodes.add(node);
				}
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			} catch (InvalidMoveException e) {
				System.out
						.println("MoveRater.getPosibleMoveContainers(): Invalid Move");
				e.printStackTrace();
			}
		}
		stageOneList = null;
		System.gc();
		// Liste mit 3 Teilzügen erstellen
		for (MoveContainer mc : stageTwoList) {
			try {
				if (System.currentTimeMillis() - time > this.MAXTIME) {
					return;
				}
				MoveContainer mcCopy = cloneMoveContainer(mc);
				GameState gs = (GameState) this.startGameState.clone();
				mcCopy.firstMove.perform(gs, gs.getCurrentPlayer());
				mcCopy.secondMove.perform(gs, gs.getCurrentPlayer());
				for (Move m : gs.getPossibleMoves()) {
					if (System.currentTimeMillis() - time > this.MAXTIME) {
						return;
					}
					MoveContainer container = cloneMoveContainer(mcCopy);
					container.addMove(m);
					// stageThreeList.add(container);
					Node node = new Node(container);
					possibleMoveNodes.add(node);
				}
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			} catch (InvalidMoveException e) {
				System.out
						.println("MoveRater.getPosibleMoveContainers(): Invalid Move");
				e.printStackTrace();
			}
		}
		stageTwoList = null;
		System.gc();
		// stageOneList.addAll(stageTwoList);
		// stageOneList.addAll(stageThreeList);
		// return stageOneList;
		return;
	}

	private MoveContainer cloneMoveContainer(MoveContainer mc) {
		MoveContainer clone = new MoveContainer();
		try {
			if (mc.firstMove != null)
				clone.firstMove = (Move) mc.firstMove.clone();
			if (mc.secondMove != null)
				clone.secondMove = (Move) mc.secondMove.clone();
			if (mc.thirdMove != null)
				clone.thirdMove = (Move) mc.thirdMove.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clone;

	}

}
