package sc.plugin2014.moves;

import java.util.*;
import sc.plugin2014.GameState;
import sc.plugin2014.converters.LayMoveConverter;
import sc.plugin2014.entities.*;
import sc.plugin2014.exceptions.InvalidMoveException;
import sc.plugin2014.exceptions.StoneBagIsEmptyException;
import sc.plugin2014.laylogic.LayLogicFacade;
import sc.plugin2014.laylogic.PointsCalculator;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 * Repräsentiert einen Legezug.
 * 
 * @author ffi
 * 
 */
@XStreamAlias(value = "laymove")
@XStreamConverter(LayMoveConverter.class)
public class LayMove extends Move implements Cloneable {

	private final Map<Stone, Field> stoneToFieldMapping;

	/**
	 * Erzeugt ein neues LayMove Objekt. {@link #stoneToFieldMapping} wird dabei
	 * als {@link HashMap} initialisiert.
	 */
	public LayMove() {
		stoneToFieldMapping = new HashMap<Stone, Field>();
	}

	/**
	 * Liefert eine Map mit Steinen, welche auf die dazugehörigen Felder gelegt
	 * werden sollen.
	 * 
	 * @return
	 */
	public Map<Stone, Field> getStoneToFieldMapping() {
		return stoneToFieldMapping;
	}

	/**
	 * Legt einen übergebenen Stein auf das übergebene Feld
	 * 
	 * @param stone
	 * @param field
	 */
	public void layStoneOntoField(Stone stone, Field field) {
		checkFieldAndStoneNotNull(stone, field);

		getStoneToFieldMapping().put(stone, field);
	}

	/**
	 * Überprüft ob einer der übergebenen Parameter null ist.
	 * 
	 * @param stone
	 * @param field
	 */
	private void checkFieldAndStoneNotNull(Stone stone, Field field) {
		if (stone == null) {
			throw new IllegalArgumentException("Stein darf nicht null sein");
		}

		if (field == null) {
			throw new IllegalArgumentException("Feld darf nicht null sein");
		}
	}

	/**
	 * Löscht die Map mit Zügen.
	 */
	public void clearStoneToFieldMapping() {
		getStoneToFieldMapping().clear();
	}

	@Override
	public void perform(GameState state, Player player)
			throws InvalidMoveException, StoneBagIsEmptyException {
		super.perform(state, player);

		checkAtLeastOneStone();

		checkIfStonesAreFromPlayerHand(getStonesToLay(), player);

		LayLogicFacade.checkIfLayMoveIsValid(getStoneToFieldMapping(),
				state.getBoard(), !state.getBoard().hasStones());

		int points = PointsCalculator.getPointsForLayMove(stoneToFieldMapping,
				state.getBoard());

		player.addPoints(points);

		List<Integer> freePositions = new ArrayList<Integer>();

		int stonesToLaySize = getStonesToLay().size();

		for (int i = 0; i < stonesToLaySize; i++) {
			Stone stoneToLay = getStonesToLay().get(i);

			freePositions.add(player.getStonePosition(stoneToLay));
			player.removeStone(stoneToLay);

			Field field = getStoneToFieldMapping().get(stoneToLay);
			state.layStone(stoneToLay, field.getPosX(), field.getPosY());
		}

		for (int i = 0; i < stonesToLaySize; i++) {
			Stone drawStone = state.drawStone();
			if (drawStone != null) {
				player.addStone(drawStone, freePositions.get(i));
			} else {
				state.updateStonesInBag();
				throw new StoneBagIsEmptyException("Der Beutel ist leer.");
			}
		}

		state.updateStonesInBag();
	}

	/**
	 * Liefert eine Liste mit Steinen, welche gelegt werden sollen.
	 * 
	 * @return Die Liste mit Steinen.
	 */
	private List<Stone> getStonesToLay() {
		return new LinkedList<Stone>(getStoneToFieldMapping().keySet());
	}

	/**
	 * Überprüft ob mindestens ein Eintrag in der Map vorhanden ist.
	 * 
	 * @throws InvalidMoveException
	 */
	private void checkAtLeastOneStone() throws InvalidMoveException {
		if (getStoneToFieldMapping().keySet().isEmpty()) {
			throw new InvalidMoveException(
					"Es muss mindestens 1 Stein gesetzt werden");
		}
	}

	/** Klont dieses Objekt. (deep-copy)
	 * @see sc.plugin2014.moves.Move#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		LayMove clone = new LayMove();
		Set<Stone> keys = this.stoneToFieldMapping.keySet();
		for (Stone stone : keys) {
			Field value = stoneToFieldMapping.get(stone);
			clone.layStoneOntoField((Stone) stone.clone(),
					(Field) value.clone());
		}
		return clone;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LayMove) {
			LayMove lm = (LayMove) obj;
			LinkedList<Stone> keys = new LinkedList<Stone>(
					stoneToFieldMapping.keySet());
			LinkedList<Stone> keysCp = new LinkedList<Stone>(
					lm.stoneToFieldMapping.keySet());
			for (Stone stone : keys) {
				if (!(keysCp.contains(stone))) {
					return false;
				}
				Stone equi = new Stone();
				for (Stone s : keysCp) {
					if (s.equals(stone)) {
						equi = s;
					}
				}
				if (!stoneToFieldMapping.get(stone).equals(
						lm.stoneToFieldMapping.get(equi))) {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}

}
