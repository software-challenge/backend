package sc.plugin2012;


import static sc.plugin2012.util.Constants.MAX_SEGMENT_SIZE;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sc.plugin2012.util.Constants;
import sc.plugin2012.util.InvalideMoveException;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias(value = "mh:select")
public class SelectMove extends Move {

	@XStreamAsAttribute
	public final MoveType moveType = MoveType.SELECT;

	// gewaehlte segmentgroessen
	@XStreamImplicit(itemFieldName = "selection")
	private final Set<Selection> selections;

	public SelectMove(List<Selection> selections) {
		this.selections = new HashSet<Selection>(selections);
	}

	public SelectMove(int[] selections) {
		this.selections = new HashSet<Selection>();
		for (int i = 0; i < selections.length; i++) {
			this.selections.add(new Selection(i + 1, selections[i]));
		}
	}

	@Override
	void perform(GameState state, Player player) throws InvalideMoveException {

		Segment[] segments = new Segment[MAX_SEGMENT_SIZE];
		for (int i = 1; i <= MAX_SEGMENT_SIZE; i++) {
			segments[i - 1] = player.getSegment(i);
		}
		int selectionSum = 0;
		for (Selection selection : selections) {
			selectionSum += selection.amount;
			if (segments[selection.size - 1].getRetained() < selection.amount) {
				throw new InvalideMoveException(player.getDisplayName()
						+ " hat nicht genug Bauelemente der Groesse "
						+ selection.size);
			}
		}

		if (selectionSum != Constants.SELECTION_SIZE) {
			throw new InvalideMoveException(player.getDisplayName()
					+ " hat nicht genau " + Constants.SELECTION_SIZE
					+ " Bauelemente gewaehlt");
		}

		for (Selection selection : selections) {
			segments[selection.size - 1].select(selection.amount);
		}

	}

	@Override
	MoveType getMoveType() {
		return moveType;
	}
}
