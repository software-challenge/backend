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
	private int MAXTIME = 3200;
	private double time;

	public MoveRater(){
		possibleMoveNodes = new LinkedList<Node>();
	}

	public MoveContainer getBestNextMove(GameState gs) {
		try {
			this.startGameState = (GameState) gs.clone();
			this.possibleMoveNodes = new LinkedList<Node>();
		} catch (CloneNotSupportedException e1) {
			e1.printStackTrace();
		}
		this.time = System.currentTimeMillis();
		for (MoveContainer mc : getPossibleMoveContainers()) {
			try {
				Node node = new Node((GameState) this.startGameState.clone(),
						mc);
				possibleMoveNodes.add(node);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Number of Nodes: " + possibleMoveNodes.size());
		int bestRating = -Rating.WIN;
		Node bestNode = null;
		for (Node n : possibleMoveNodes) {
			int rating = n.rateMove();
			if(rating > bestRating){
				bestRating = rating;
				bestNode = n;
			}
			if(System.currentTimeMillis() - time > this.MAXTIME){
				break;
			}
		}
		if(bestNode == null){
			bestNode = new Node(startGameState, new MoveContainer());
		}
		System.out.println("It took me " + (System.currentTimeMillis() -this.time) + " ms to finish");
		System.out.println("Number of Nodes: " + possibleMoveNodes.size());
		return bestNode.getMoveToPerform();
	}

	public List<MoveContainer> getPossibleMoveContainers() {
		List<Move> list = this.startGameState.getPossibleMoves();
		LinkedList<MoveContainer> stageOneList = new LinkedList<MoveContainer>();
		LinkedList<MoveContainer> stageTwoList = new LinkedList<MoveContainer>();
		LinkedList<MoveContainer> stageThreeList = new LinkedList<MoveContainer>();
		// Move Container mit nur einem Move
		for (Move m : list) {
			stageOneList.add(new MoveContainer(m));
		}
		// Liste mit 2 Teilzügen erstellen
		for (MoveContainer mc : stageOneList) {
			try {
				MoveContainer mcCopy = cloneMoveContainer(mc);
				GameState gs = (GameState) this.startGameState.clone();
				mcCopy.firstMove.perform(gs, gs.getCurrentPlayer());
				for (Move m : gs.getPossibleMoves()) {
					MoveContainer container = cloneMoveContainer(mcCopy);
					container.addMove(m);
					stageTwoList.add(container);
				}
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			} catch (InvalidMoveException e) {
				System.out
						.println("MoveRater.getPosibleMoveContainers(): Invalid Move");
				e.printStackTrace();
			}
		}
		// Liste mit 3 Teilzügen erstellen
		for (MoveContainer mc : stageTwoList) {
			try {
				MoveContainer mcCopy = cloneMoveContainer(mc);
				GameState gs = (GameState) this.startGameState.clone();
				mcCopy.firstMove.perform(gs, gs.getCurrentPlayer());
				mcCopy.secondMove.perform(gs, gs.getCurrentPlayer());
				for (Move m : gs.getPossibleMoves()) {
					MoveContainer container = cloneMoveContainer(mcCopy);
					container.addMove(m);
					stageThreeList.add(container);
				}
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			} catch (InvalidMoveException e) {
				System.out
						.println("MoveRater.getPosibleMoveContainers(): Invalid Move");
				e.printStackTrace();
			}
		}
		stageOneList.addAll(stageTwoList);
		stageOneList.addAll(stageThreeList);
		return stageOneList;

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
