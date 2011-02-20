package sc.plugin2012;

import sc.plugin2012.util.InvalideMoveException;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value = "manhattan:build")
public class BuildMove extends Move {

	@XStreamAsAttribute
	public final int city;

	@XStreamAsAttribute
	public final int slot;

	@XStreamAsAttribute
	public final int size;

	public BuildMove(int city, int slot, int size) {
		this.city = city;
		this.slot = slot;
		this.size = size;
	}

	@Override
	void perform(GameState state, Player player) throws InvalideMoveException {
		Tower tower = state.getTower(city, slot);
		PlayerColor color = player.getPlayerColor();
		Segment segment = player.getSegment(size);

		if (segment == null || segment.getUsable() < 1) {
			throw new InvalideMoveException(player.getDisplayName() + " hat kein Bauelement der Groesse "
					+ size);
		}

		if (!player.hasCard(slot)) {
			throw new InvalideMoveException(player.getDisplayName() + " hat keine Karte fuer den Bauplatz "
					+ slot);
		}

		if (!tower.addPart(color, size)) {
			throw new InvalideMoveException("Das gewaehltes Element war nciht gross genug");
		}

		segment.use();
		player.removeCard(slot);
		player.addCard(state.drawCard());

	}

	@Override
	public MoveType getMoveType() {
		return MoveType.BUILD;
	}

}
