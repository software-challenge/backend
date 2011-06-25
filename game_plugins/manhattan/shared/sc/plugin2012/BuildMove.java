package sc.plugin2012;

import sc.plugin2012.util.BuildMoveConverter;
import sc.plugin2012.util.InvalideMoveException;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias(value = "manhattan:build")
@XStreamConverter(BuildMoveConverter.class)
public class BuildMove extends Move {

	public final int city;

	public final int slot;

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
			throw new InvalideMoveException(player.getDisplayName() + " hat kein Bauelement der Größe " + size);
		}

		if (!player.hasCard(slot)) {
			throw new InvalideMoveException(player.getDisplayName() + " hat keine Karte für den Bauplatz "
					+ slot);
		}

		if (!tower.addPart(color, size)) {
			throw new InvalideMoveException("Das gewähltes Element war nicht groß genug");
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
