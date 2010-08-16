package sc.plugin_minimal;

import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * ein spielzug
 * 
 * @author sca, tkra
 */
@XStreamAlias(value = "minimal:move")
public final class Move {

	// das betroffene schaf
	public final Sheep sheep;

	// das spielfeld auf das das betroffene schaf ziehen soll
	public final Node target;

	public Move(Sheep hat, Node target) {
		this.sheep = hat;
		this.target = target;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Move) {
			Move other = (Move) obj;
			return other.sheep == sheep && other.target == target;
		}
		return false;
	}

	/**
	 * liefert den spieler dem das betroffene schaf hehoert
	 */
	public Player getOwner() {
		return sheep.owner;
	}

	/**
	 * gibt an ob dies ein gueltiger spielzug ist, wenn der spieler dem das
	 * betroffene schaf gehoert zur zeit am zug waere
	 */
	public boolean isValide() {
		return sheep.getValideMoves().keySet().contains(this);
	}

	/**
	 * fuehrt diesen spielzug aus
	 */
	public void perform() {

		// den verwendeten wuerfel aud dem vorrat entfernen
		Map<Node, Integer> validMoves = sheep.getValideMoves();
		sheep.getNode().getBoard().removeDice(validMoves.get(target));

		// jedes schaf das auf dem zielfeld steht und dem gegenspielr gehoet
		// wird einverleibt. die blumen werden uebernommen. der geleitschutz
		// wird oebernommen
		for (Sheep victim : target.getSheeps()) {
			if (victim.owner != sheep.owner) {
				sheep.getSize().add(victim.getSize());
				sheep.addFlowers(victim.getFlowers());
				if (victim.hasSheepdog()) {
					sheep.setSheepdog(true);
				}

				if (victim.hasSharpSheepdog()) {
					sheep.setSharpSheepdog(true);
				}

				victim.kill();
			}

		}

		// das schaf wird auf das zielfeld bewegt
		target.addSheep(sheep);
		sheep.getNode().removeSheep(sheep);
		sheep.setNode(target);

		switch (target.getNodeType()) {
		case GRASS:
		case FENCE:
			// auf normalen feldern werden die blumen aufgesammelt
			sheep.addFlowers(target.getFlowers());
			target.addFlowers(-target.getFlowers());
			break;

		case HOME1:
		case HOME2:
			// wenn ein heimatfeld betreten wird werden die gesammelten blumen
			// und gegnerischen schafe gesichert ...
			sheep.owner.addCapturedFlowers(sheep.getFlowers());
			sheep.owner.addCapturedSheeps(sheep.getSize().getSize(
					sheep.owner.getOponentColor()));

			// ... und die eigenen gesammelten schafe freigelassen
			int n = sheep.getSize().getSize(sheep.owner.getPlayerColor()) - 1;
			for (int i = 0; i < n; i++) {
				new Sheep(target, target.getCounterPart(), sheep.owner);
			}

			// wurde der schaeferhund eingesammelt bleibt er bei diesem schaf
			// und wird scharf
			sheep.getSize().reset();
			sheep.getSize().add(sheep.owner.getPlayerColor());
			if (sheep.hasSheepdog()) {
				sheep.getSize().add(PlayerColor.NOPLAYER);
				sheep.setSharpSheepdog(true);
				sheep.setSheepdog(false);
			}

			// das neue ziel ist das gegenueberliegende heimatfeld
			sheep.setTarget(target.getCounterPart());

			// die gesammelten blumen werden weggenommen
			sheep.addFlowers(-sheep.getFlowers());
			break;
		}

	}

}
