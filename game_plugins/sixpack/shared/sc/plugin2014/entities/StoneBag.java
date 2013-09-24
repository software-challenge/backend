package sc.plugin2014.entities;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

import sc.plugin2014.GameState;
import sc.plugin2014.util.Constants;

public class StoneBag implements Cloneable {

	private final List<Stone> stones;
	private final List<Stone> nextStones;
	
	private SecureRandom sr;
	
	public StoneBag()  {
		stones = new ArrayList<Stone>(Constants.STONES_COLOR_COUNT
				* Constants.STONES_SHAPE_COUNT
				* Constants.STONES_SAME_KIND_COUNT);
		nextStones = new ArrayList<Stone>(12);

		for (int i = 0; i < Constants.STONES_COLOR_COUNT; i++) {
			for (int j = 0; j < Constants.STONES_SHAPE_COUNT; j++) {
				for (int k = 0; k < Constants.STONES_SAME_KIND_COUNT; k++) {
					stones.add(new Stone(StoneColor.getColorFromIndex(i),
							StoneShape.getShapeFromIndex(j)));
				}
			}
		}
		try {
			sr = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			sr = new SecureRandom();
		}
		sr.nextBytes(new byte[1]);
		randomizeStones();

		refreshNextStones();
	}

	private void refreshNextStones() {
		if (nextStones.size() == Constants.STONES_OPEN_FROM_BAG_COUNT) {
			return;
		}

		while (!stones.isEmpty()
				&& (nextStones.size() < Constants.STONES_OPEN_FROM_BAG_COUNT)) {
			nextStones.add(stones.remove(0));
		}
	}

	private void randomizeStones() {
		Collections.shuffle(stones, sr);
	}

	public int getStoneCountInBag() {
		return stones.size() + nextStones.size();
	}

	public Stone drawStone() {
		if (nextStones.size() > 0) {
			Stone result = nextStones.remove(0);

			refreshNextStones();

			return result;
		} else {
			return null;
		}
	}

	public void putBackStone(Stone stone) {
		stones.add(stone);
		randomizeStones();
		refreshNextStones();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		StoneBag clone = new StoneBag();
		clone.stones.clear();
		clone.nextStones.clear();
		for (Stone s : stones) {
			clone.stones.add((Stone) s.clone());
		}
		for (Stone s : nextStones) {
			clone.nextStones.add((Stone) s.clone());
		}
		return clone;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StoneBag) {
			StoneBag bag = (StoneBag) obj;
			for (Stone stone : stones) {
				if (!bag.stones.contains(stone)) {
					return false;
				}
			}
			for (Stone stone : nextStones) {
				if (!bag.nextStones.contains(stone)) {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}

	public List<Stone> getNextStonesInBag() {
		return nextStones;
	}

	public void loadFromFile(GameState gs) {
		List<Stone> blueStones = gs.getBluePlayer().getStones();
		List<Stone> redStones = gs.getRedPlayer().getStones();
		List<Stone> openStones = gs.getNextStonesInBag();

		ArrayList<Stone> tempStones = new ArrayList<Stone>(
				Constants.STONES_COLOR_COUNT * Constants.STONES_SHAPE_COUNT
						* Constants.STONES_SAME_KIND_COUNT);

		for (Stone stone : redStones) {
			tempStones.add(stone);
		}
		for (Stone stone : blueStones) {
			tempStones.add(stone);
		}
		for (Stone stone : openStones) {
			tempStones.add(stone);
		}

		for (Stone stone : nextStones) {
			stones.add(stone);
		}

		// filter out stones
		for (Stone bagStone : stones) {
			if (!tempStones.contains(bagStone)) {
				tempStones.add(bagStone);
			}
		}

		// filter out the stones already laid
		if (gs.getBoard().hasStones()) {
			for (Field field : gs.getBoard().getFields()) {
				if (!field.isFree()) {
					tempStones.remove(field.getStone());
				}
			}
		}

		stones.clear();
		nextStones.clear();

		for (Stone stone : tempStones) {
			stones.add(stone);
		}

		refreshNextStones();
	}
}
